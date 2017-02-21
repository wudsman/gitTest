#ifndef QUEUEARRAY_H_
#define QUEUEARRAY_H_

#include<vector>
#include<queue>

template<class T> class QueueArray
{
public:

// queueArray constructor
// initialize v to specified size and myArray to an empty vector
    QueueArray(int size)
    {
        for(int i=0; i<size;i++)
        {
            std::queue<T> q;
            v.push_back(q);
        }
        myArray = new T[0];
    }

// queuArray destructor
// deallocate v and myArray
    ~QueueArray()
    {
        delete [] myArray;
    }

// add an item of type T to the queue at the specified index
// return -1 if index is out of range, 1 if successfully enqueued, 0 otherwise
    int Enqueue(T item, int index)
    {
        if(index >= v.size()|| index < 0)
            return -1;

        else
        {
            v[index].push(item);
            return 1;
        }

        return 0;
    }

// dequeue an element from the first non-empty queue
// if and item is dequeued, return the value of the item, else return 0
    T Dequeue()
    {
        for(int i=0;i<v.size(); i++)
        {
            if(v[i].empty() == false)
            {
                T tmp = v[i].front();
                v[i].pop();
                return tmp;
            }
        }

        return 0;
    }

// return the size of the queue at the specified index
// if index is out of range, return -1
    int Qsize(int index)
    {
        if(index >= v.size() || index < 0)
            return -1;

        else
            return v[index].size();
    }

// return the number of queues in the vector
    int Asize()
    {
      return v.size();
    }

// return the total number of elements in the queue vector
    int QAsize()
    {
        int total(0);

        for(int i=0;i<v.size();i++)
        {
            total += v[i].size();
        }

        return total;
    }

// return a pointer to an array filled with the contents of the queue at the specified index
    T* Qstate(int index)
    {
        int counter(0);

        if(index >= v.size())
            return NULL;

        else
        {

            std::queue<T> tmp = v[index];
            delete [] myArray;
            myArray =  new T[tmp.size()];

            while(!(tmp.empty()))
            {
                T item = tmp.front();
                myArray[counter] = item;
                tmp.pop();
                counter ++;
            }

            return myArray;
        }
    }

private:
    std::vector<std::queue<T> > v;
    T *myArray;

};


#endif // QUEUEARRAY_H_INCLUDED
