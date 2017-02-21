// Lucas Woodbury
// COSC 3020
// Project 4 - Task 4
// 7 December 2015
// Code for Approximate String Matching algorithm

#include "unixTimer.h"

#include<iostream>
#include<string>
#include<vector>
#include<stack>
#include<climits>

using namespace std;

typedef unsigned int uint;

// simple function for finding the minimum of three ints
int minimum(int a, int b, int c)
{
    int tmp = (a < b) ? a:b;

    return (tmp < c) ? tmp:c;
}

// function for calculating the edit distance between two characters
// returns 0 or 1
int dist(char a, char b)
{
    if(a==b)
        return 0;
    else
        return 1;
}

int main()
{
	string S;
	string P;
	int k;
    Timer time;

	// prompt for input
	cout << "Enter some text with no white space." << endl;
	cin >> S;

	cout << "Enter a pattern string." << endl;
	cin >> P;

	cout << "Enter the maximum number of mismatches allowed." << endl;
	cin >> k;

    time.start();

    // variables for length of S and P
    int Slength, Plength;

    // Slength + 1 to account for the empty character in the matrix
    Slength = S.length()+1;
    Plength = P.length();

    // initialize matrix for calculating edit distance
	vector<vector<int>> matrix(Plength,vector<int>(Slength));

    // set edit distance of first row of matrix
	for(int i = 0; i< Slength;i++)
    {
        if(i==0)
        {
            matrix[0][i] = 1;
        }
        else
        {
            matrix[0][i] = dist(P[0],S[i-1]);
        }
    }

    // set edit distance of first column of matrix
    // all substrings in P compared to empty char,
    // so edit distance of all substrings is the length of the substring
    // by this logic, the comparison is not performed
    for(int i = 0; i< Plength;i++)
    {
        matrix[i][0] = i + 1;
    }

    // calculate the edit distance for all indices in the matrix
    // time is n*m
	for(int i = 1; i< Plength;i++)
    {
        for (int j = 1; j< Slength;j++)
        {
            matrix[i][j] = minimum(matrix[i-1][j]+1,matrix[i][j-1]+1, dist(S[j-1],P[i])+matrix[i-1][j-1]);
        }
    }

    // this code is for printing out a formatted version of the matrix
    // not effective if any integer has two or more digits
    /*  cout << "  " << S << endl;
    for(int i=0;i<Plength;i++)
    {
        cout << P[i];

        for (int j = 0; j<Slength;j++)
        {
            cout << matrix[i][j];
        }
        cout << endl;
    }*/


    // location of last character of the first occurrence of the ASM
    int ASMlastindex(INT_MAX);

    // find the the location of ASMlastindex
    // break when found
    for(int i=0;i< Slength;i++)
    {
        if(matrix[Plength-1][i] <= k)
        {
            ASMlastindex = i;
            break;
        }
    }

    if(ASMlastindex > Slength)
    {
        cout << "No pattern found within the the given limit for approximation. ";
        exit(0);
    }

    int a(Plength -1);
    int b(ASMlastindex);

    // stack for reversing order of output characters
    stack<char> chStack;

    // retreive characters of ASM and place them in the stack
    while(a!=0 && b!=0)
    {

        if(matrix[a][b] == matrix [a][b-1] + 1)
        {
            chStack.push(S[b-1]);
            b--;
        }
        else if(matrix[a][b] == matrix[a-1][b]+1)
        {
            a--;
        }
        else
        {
            chStack.push(S[b-1]);
            a--;
            b--;
        }


    }
    chStack.push(S[b-1]);

    // the first approximate string match in S
    string ASM;

    // place characters in stack in a correctly-ordered string
    while(!chStack.empty())
    {
        ASM += chStack.top();
        chStack.pop();
    }

    time.stop();

    //output results.
    cout << "The first approximate match is: "
        << ASM << endl;
    cout << "Found from indices "
        << ASMlastindex - ASM.length() << " to "
        << ASMlastindex - 1 << ". " << endl;
    cout << "Found in " << time() << " seconds." << endl;

	// pause program
    //cin.get();

	return 0;
}
