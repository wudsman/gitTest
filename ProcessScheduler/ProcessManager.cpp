// Lucas Woodbury
// COSC 4740
// Program2

#include <cstdio>
#include <cstdlib>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <iostream>
#include <stdlib.h>
#include <math.h>

#include "QueueArray.h"

//#include "PCB.h"
struct PCB
{
public:
    PCB(int p, int val, int start, int run)
        : value(val), start_time(start), run_time(run), pid(p),
          priority(0), quantum(1), cpu_time(0), end_time(-1) {}
    int pid;
    int value;
    int start_time;
    int run_time;
    int priority;
    int quantum;
    int cpu_time;
    int end_time;
};

int main(int argc, char *argv[])
{
    const int BUFFER_SIZE(10), WRITE_END(1), READ_END(0);

    int cpipe[2], turnTimeTotal(0), turnTimeCounter(0);

    char cmnd;

    // variable and containers for storing processes
    int Time = 0 , RunningState = -1;
    std::vector<PCB> PcbTable;
    PcbTable.reserve(100);
    QueueArray <int> ReadyState(4);
    QueueArray <int> BlockedState0(4);
    QueueArray <int> BlockedState1(4);
    QueueArray <int> BlockedState2(4);

    // for storing a PCB pointer
    PCB *pcb;

    // push and empty PCB into address 0
    //PcbTable.push_back(*pcb);

    // convert parameters in argv[] to pointers
    cpipe[READ_END] = atoi(argv[1]);
    cpipe[WRITE_END] = atoi(argv[2]);

    //close write side of the pipe
    close(cpipe[WRITE_END]);

	// reads from pipe
    read(cpipe[READ_END], (void *)&cmnd, sizeof(char));

    while( cmnd !='T')
    {
        char cmndOp;
        int a1, a2, a3;
        // test first letter against cases
        switch(cmnd)
        {
        case 'S':
            read(cpipe[READ_END], (void *)&a1, sizeof(int));
            read(cpipe[READ_END], (void *)&a2, sizeof(int));
            read(cpipe[READ_END], (void *)&a3, sizeof(int));

            // create new PCB
            pcb= new PCB(a1, a2, Time, a3);
            // add process to PcbTable
            PcbTable[a1] = *pcb;
            // if no running process, put this on the cpu
            if(RunningState < 0)
                RunningState = a1;
            else
                ReadyState.Enqueue(a1, 0);
            break;
        case 'B':
            read(cpipe[READ_END], (void *)&a1, sizeof(int));

            // reset priority
            if(PcbTable[RunningState].priority > 0){
                PcbTable[RunningState].priority--;
                PcbTable[RunningState].quantum = pow(2,PcbTable[RunningState].priority);
            }

            // enqueue blocked process in appropriate QueueArray
            switch (a1)
            {
            case 0:
                BlockedState0.Enqueue(RunningState,PcbTable[RunningState].priority);
                break;
            case 1:
                BlockedState1.Enqueue(RunningState,PcbTable[RunningState].priority);
                break;
            case 2:
                BlockedState2.Enqueue(RunningState,PcbTable[RunningState].priority);
                break;
            }

            // set new RunningState
            RunningState = ReadyState.Dequeue();
            break;
        case 'U':
            read(cpipe[READ_END], (void *)&a1, sizeof(int));
            int tmp;

            // remove RunningState from BlockedState
            switch (a1)
            {
            case 0:
                tmp = BlockedState0.Dequeue();
                break;
            case 1:
                tmp = BlockedState1.Dequeue();
                break;
            case 2:
                tmp = BlockedState2.Dequeue();
                break;
            default:
                perror("invalid rid");
                exit(1);
            }

            //place process in ReadyState
            ReadyState.Enqueue(tmp,PcbTable[tmp].priority);
            break;
        case 'C':
            read(cpipe[READ_END], (void *)&cmndOp, sizeof(char));
            read(cpipe[READ_END], (void *)&a1, sizeof(int));

            // match the command for the correct operation
            switch (cmndOp)
            {
            case 'A':
                PcbTable[RunningState].value += a1;
                break;
            case 'S':
                PcbTable[RunningState].value -= a1;
                break;
            case 'M':
                PcbTable[RunningState].value *= a1;
                break;
            case 'D':
                PcbTable[RunningState].value /= a1;
                break;
            default:
                perror("Operation argument not found");
                exit(1);
            }
        case 'Q':


            // increment cpu_time of running process and time
            // decrement quantum of running process
            Time++;
            PcbTable[RunningState].cpu_time++;
            PcbTable[RunningState].quantum--;

            //  if cpu_time equals run_time of running process, it us done
            // place a new process in RunningState
            if(PcbTable[RunningState].cpu_time == PcbTable[RunningState].run_time){
                PcbTable[RunningState].end_time = Time;
                RunningState = ReadyState.Dequeue();
            }

            // if running process's quantum has ended, increment the priority,
            // move it to ReadyState, place a new process in RunningState
            if(PcbTable[RunningState].quantum == 0){
                if(PcbTable[RunningState].priority < 3){
                    PcbTable[RunningState].priority++;
                }
                PcbTable[RunningState].quantum = pow(2,PcbTable[RunningState].priority);
                ReadyState.Enqueue(RunningState, PcbTable[RunningState].priority);
                RunningState = ReadyState.Dequeue();
            }
            break;
        case 'P':
            std::cout << "Running P" << std::endl;
            int child;

            // fork child
            if((child = fork())==-1){
                perror("unable to fork reporter child");
                exit(1);
            }
            else if (child == 0){
                // print state report
                std::cout << "CURRENT TIME: " << Time << std::endl;
                std::cout << "RUNNING PROCESS: " << RunningState << "< " << PcbTable[RunningState].value<<" , " << PcbTable[RunningState].start_time<< " , " << PcbTable[RunningState].cpu_time << " >\n";
                std::cout << "BLOCKED PROCESSES:" << std::endl
                << "Queue of processes blocked for resource 0:\n";
                for(int i=0; i < BlockedState0.Asize();i++){
                    int *arr;
                    arr = BlockedState0.Qstate(i);
                        for(int j = 0; j< BlockedState0.Qsize(i);j++){
                            int tmp = arr[j];
                            std::cout << "<" << PcbTable[tmp].pid <<" , " << PcbTable[tmp].priority << " , " << PcbTable[tmp].value << " , " << PcbTable[tmp].start_time << " , " << PcbTable[tmp].cpu_time << ">\n";
                        }
                }
                std::cout << "Queue of processes blocked for resource 1:\n";
                for(int i=0; i < BlockedState1.Asize(); i++){
                    int *arr;
                    arr = BlockedState1.Qstate(i);
                        for(int j =0; j< BlockedState1.Qsize(i);j++){
                            int tmp = arr[j];
                            std::cout << "<" << PcbTable[tmp].pid <<" , " << PcbTable[tmp].priority << " , " << PcbTable[tmp].value << " , " << PcbTable[tmp].start_time << " , " << PcbTable[tmp].cpu_time << ">\n";
                        }
                }
                std::cout << "Queue of processes blocked for resource 2:\n";
                for(int i=0; i < BlockedState2.Asize();i++){
                    int *arr;
                    arr = BlockedState2.Qstate(i);
                        for(int j =0; j< BlockedState2.Qsize(i);j++){
                            int tmp = arr[j];
                            std::cout << "<" << PcbTable[tmp].pid <<" , " << PcbTable[tmp].priority << " , " << PcbTable[tmp].value << " , " << PcbTable[tmp].start_time << " , " << PcbTable[tmp].cpu_time << ">\n";
                        }
                }
                std::cout << "PROCESSES READY TO EXECUTE:" << std::endl;
                for(int i=0; i < ReadyState.Asize();i++){
                    std::cout << "Queue of processes with priority " << i << ": " << std::endl;
                        int *arr;
                        arr = ReadyState.Qstate(i);
                        for(int j =0; j< ReadyState.Qsize(i);j++){
                            int tmp = arr[j];
                            std::cout << "<" << PcbTable[tmp].pid <<" , " << PcbTable[tmp].priority << " , " << PcbTable[tmp].value << " , " << PcbTable[tmp].start_time << " , " << PcbTable[tmp].cpu_time << ">\n";
                        }
                }
                exit(1);
            }
            break;
        //case 'T':
          //  break;
        default:
            perror("input not found");
            exit(1);
        }
        read(cpipe[READ_END], (void *)&cmnd, sizeof(char));
    }


    // compute and report average turnaround time
    // used constant 50 because PcbTable.size() didn't work
    for(int i =0; i< 50; i++){
        if(PcbTable[i].end_time > 0){
            turnTimeTotal += (PcbTable[i].end_time - PcbTable[i].start_time);
            turnTimeCounter++;
        }
    }
    std::cout << turnTimeTotal << " " << turnTimeCounter << std::endl;
    std::cout << "Average turnaround time: " << turnTimeTotal/(float)turnTimeCounter << std::endl;
    return(0);
}
