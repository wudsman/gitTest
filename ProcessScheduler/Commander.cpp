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



int main(int args, char* argv[])
{
    // some constants
    const int BUFFER_SIZE = 10, WRITE_END = 1, READ_END = 0;

    // child process
    int child, a1, a2, a3;

    //  the pipe
    int cpipe[2];

    //bool for testing if read is successful
    bool done(false);

    // used to pass pipe address to the child process

    char mc0[10], mc1[10], cmnd, cmndOp;

    // create the pipe
    if(child = pipe(cpipe)) {
        perror("unable to create pipe");
        exit(1);
    }

    // create string values for the pipe addresses
    sprintf(mc0, "%d", cpipe[0]);
    sprintf(mc1, "%d", cpipe[1]);

    // create the child process
    if((child = fork()) == -1){
        perror("unable to fork child");
        exit(1);
    }
    else if (child == 0) {
        // child process

        // exec ProcessManager and pass the pipe information
        execl("ProcessManager","ProcessManager",mc0,mc1, NULL);

        // if execl fails
        exit(1);
    }

    // close the read side of the pipe
    close(cpipe[READ_END]);

	// read in the characters
    while(std::cin >> cmnd){
        switch(cmnd)
        {
        case 'S':
            std::cin >> a1 >> a2 >> a3;
            write(cpipe[WRITE_END],(void *)&cmnd, sizeof(char));
            write(cpipe[WRITE_END],(void *)&a1, sizeof(int));
            write(cpipe[WRITE_END],(void *)&a2, sizeof(int));
            write(cpipe[WRITE_END],(void *)&a3, sizeof(int));
            break;
        case 'B':
            std::cin >> a1;
            write(cpipe[WRITE_END],(void *)&cmnd, sizeof(char));
            write(cpipe[WRITE_END],(void *)&a1, sizeof(int));
            break;
        case 'U':
            std::cin >> a1;
            write(cpipe[WRITE_END],(void *)&cmnd, sizeof(char));
            write(cpipe[WRITE_END],(void *)&a1, sizeof(int));
            break;
        case 'C':
            std::cin >> cmndOp >> a1;
            write(cpipe[WRITE_END],(void *)&cmnd, sizeof(char));
            write(cpipe[WRITE_END],(void *)&cmndOp, sizeof(char));
            write(cpipe[WRITE_END],(void *)&a1, sizeof(int));
            break;
        case 'Q':
            write(cpipe[WRITE_END],(void *)&cmnd, sizeof(char));
            break;
        case 'P':
            write(cpipe[WRITE_END],(void *)&cmnd, sizeof(char));
            break;
        case 'T':
            write(cpipe[WRITE_END],(void *)&cmnd, sizeof(char));
            break;
        default:
            perror("input not found");
            exit(1);
        }
        sleep(2);
    }
    wait(NULL);
    close(cpipe[READ_END]);
    return(0);
}




