

#include<iostream>
#include<string>
#include<algorithm>

#ifndef MEMORY_INCLUDED
#define MEMORY_INCLUDED

using namespace std;

class Memory
{
public:
    Memory(){fill(array, end(array), "nop");}
    Memory(Memory&) = delete;
    Memory& operator=(const Memory&) = delete;
    Memory(Memory&&) = delete;
    Memory& operator=(Memory&&) = delete;

    // get method
    string get(int i){return array[i];}

    //set method
    void set(int i, string s){array[i] = s;}

private:

    string array[1000];
};

#endif // MEMORY_INCLUDED
