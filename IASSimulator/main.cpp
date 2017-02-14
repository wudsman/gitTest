// Lucas Woodbury
// COSC 2150
// Homework #10

#include "Memory.h"
#include <regex>
#include <fstream>
#include <cstdlib>
#include <cstdio>
#include <cmath>
#include <sstream>
#include <iostream>



using namespace std;

//global variables
unsigned int PC(0),MAR(0);

int AC(0), MQ(0);

string MBR, IR;

Memory memory;

// cast a string to an integer
int getIntVal(string s)
{
    int returnInt(0);

    if(regex_search(s,regex("\\d+")))
    {
        returnInt = atoi(s.c_str());
    }

    return returnInt;
}

// cast an integer to a string
string getStrVal(int intConvert) {
  ostringstream cstr;  //create the stream
  cstr << intConvert;  //put integer into the stream
  return cstr.str();  //put out the string
}
// execute function
void execute(int instr, int addr)
{
    switch(instr)
    {
    case 0:  //load M(X)
        AC = getIntVal(memory.get(addr));
        break;
    case 1:  //stor M(X)
        memory.set(addr,getStrVal(AC));
        break;
    case 2:  //load MQ
        AC = MQ;
        break;
    case 3:  //load MQ,M(X)
        MQ = getIntVal(memory.get(addr));
        break;
    case 4:  //load -M(X)
        AC = -(getIntVal(memory.get(addr)));
        break;
    case 5:  //load |M(X)|
        AC = abs(getIntVal(memory.get(addr)));
        break;
    case 6:  //load -|M(X)|
        AC = -(abs(getIntVal(memory.get(addr))));
        break;
    case 7:  //jump M(X)
        PC = addr;
        break;
    case 8:  //jump+ M(X)
        if(AC >= 0 )
        {
            PC = addr;
        }
        break;
    case 9:  //add M(X)
        AC += getIntVal(memory.get(addr));
        break;
    case 10: //add |M(X)|
        AC += abs(getIntVal(memory.get(addr)));
        break;
    case 11: //sub M(X)
        AC -= getIntVal(memory.get(addr));
        break;
    case 12: //sub |M(X)|
        AC -= abs(getIntVal(memory.get(addr)));
        break;
    case 13: //mul M(X)
        AC = MQ * getIntVal(memory.get(addr));
        break;
    case 14: //div M(X)
        MQ = AC / getIntVal(memory.get(addr));
        AC = AC % getIntVal(memory.get(addr));
        break;
    case 15: //lsh
        AC = AC*2;
        break;
    case 16: //rsh
        AC = AC/2;
    }
}


// decode function, calls execute function
void decode()
{
    match_results<string::const_iterator> sm;

    if(regex_match(IR,sm, regex("load M\\((\\d+)\\)")))
    {
        execute(0,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("stor M\\((\\d+)\\)")))
    {
        execute(1,getIntVal(sm[1]));
    }
    else if(regex_match(IR,regex("load MQ")))
    {
        execute(2,0);
    }
    else if(regex_match(IR,sm,regex("load MQ\\,M\\((\\d+)\\)")))
    {
        execute(3,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("load \\-M\\((\\d+)\\)")))
    {
        execute(4,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("load \\|M\\((\\d+)\\)\\|")))
    {
        execute(5,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("load \\-\\|M\\((\\d+)\\)\\|")))
    {
        execute(6,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("jump M\\((\\d+)\\)")))
    {
        execute(7,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("jump\\+ M\\((\\d+)\\)")))
    {
        execute(8,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("add M\\((\\d+)\\)")))
    {
        execute(9,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("add \\|M\\((\\d+)\\)\\|")))
    {
        execute(10,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("sub M\\((\\d+)\\)")))
    {
        execute(11,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("sub \\|M\\((\\d+)\\)\\|")))
    {
        execute(12,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("mul M\\((\\d+)\\)")))
    {
        execute(13,getIntVal(sm[1]));
    }
    else if(regex_match(IR,sm,regex("div M\\((\\d+)\\)")))
    {
        execute(14,getIntVal(sm[1]));
    }
    else if(regex_match(IR,regex("lsh")))
    {
        execute(15,0);
    }
    else if(regex_match(IR,regex("rsh")))
    {
        execute(16,0);
    }
    else if(regex_match(IR,regex("\\..*")))
    {
    }
    else if(regex_match(IR,regex("\\. .*")))
    {
    }
    else if(regex_match(IR,regex("begin")))
    {
    }
    else if(regex_match(IR,regex("halt")))
    {
    }
    else if(regex_match(IR,regex("nop")))
    {
    }
    else
    {
        cout << "Instruction not found";
        exit(0);
    }
}


int main()
{

    match_results<string::const_iterator> result;

    // prompt for filename
    cout << "Enter a filename. \n";
    string file;
    cin >> file;

    string line;
    ifstream inFile(file);

    // read file into Memory
    while(getline(inFile,line))
    {
        cout << line << endl;
        if(regex_match(line, result, regex("(\\d+) (.*)")))
        {
            memory.set(getIntVal(result[1]),result[2]);

            //set PC to begin address
            if(result[2].compare("begin")==0)
            {
                PC = getIntVal(result[1]);
            }
        }
    }

    while(IR != "halt")
    {
        MAR = PC;
        MBR = memory.get(MAR);
        IR = MBR;
        cout << "PC: " << PC << "  IR: " << IR << endl;
        PC++;
        decode();
        cout << "PC: " << PC << "  AC: " << AC << "  MQ: " << MQ << endl;
    }

    return 0;
}
