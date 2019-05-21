import numpy as np
import os
import thinkstats2
from collections import OrderedDict

path = '/home/teresa/Desktop/measurementSMS/'
counter = 0
#generate matrix
gen = {} #store by index 1 -> [BSSID]

for filename in os.listdir(path):
    counter = counter + 1
    myDict ={}
    gen[counter] ={}

    BSSID = "0.0.0.0"
    level = -56

    f = open('/home/teresa/Desktop/measurementSMS/%s'%filename, "r")
    line = f.readline()
    count = 0;
    while line:
        if ((count % 2) != 0):
            #odd is level
            level = int(line)
            if (BSSID in myDict.keys()):
                myDict[BSSID].extend([level])
            else:
                myDict[BSSID] = []
                myDict[BSSID].extend([level])
        else:
            BSSID = line

        line = f.readline()
        count = count+1
    newd = {}
    newd = sorted(myDict.keys())
    cell = {}

    #od = OrderedDict(sorted(myDict.items(), key=lambda (k, v): (v, k)))


    #for key, value in myDict.items():

     #   pmf = thinkstats2.Pmf(value)

      #  print(sorted(pmf.GetDict().keys()))
       # if str in pmf.GetDict().keys():
        #    print(pmf.GetDict()[str])

    for key, value in myDict.items():
        gen[counter][key] = value


#now we have the matrix w BSSID and RSS values, we need to start with BSSID create a dict that can hold pmf
#dict will look like: "BSSID":{ "16": pmf "15": pmf}
glori={}

for key, value in gen.items():
    index = 17 -key;
    #loop through every BSSID
    for k, v in gen[key].items():
        #we get a BSSID
        if k in glori.keys():
            glori[k][index]=thinkstats2.Pmf(v)
        else:
            glori[k] = {}
            glori[k][index]=thinkstats2.Pmf(v)

str = "24:01:c7:76:27:30\n"
#print(glori[str].items())

# for key, value in glori.items():
#     print("\n")
#     print("key: ",key)
#     print("\n")
#     for k, v in glori[key].items():
#         print( "cell:", k, " :", v)

#now I have glori but it should have a fixed format, I am going to consider values from -38 to -94
hey = np.arange(-38, -94, -1) #reference, future will be np.arange(0, 255, 1)
row = np.zeros(len(hey))

matrix = {}

for key, value in glori.items():
    matrix[key] = {}
    for k, v in glori[key].items():
        matrix[key][k] = row

# for key, value in matrix.items():
#     print("\n")
#     print("key: ",key)
#     print("\n")
#     for k, v in matrix[key].items():
#         print( "cell:", k, " :", v)


str = "10:44:00:3f:d9:dd\n"
count = 0
for key, value in glori.items():
    for k, v in glori[key].items():
        if (key == str):
            print("cell:", k)
        #v.GetDict().items():
        for kk, vv in glori[key][k].GetDict().items():
            #if (key == str):
                #print(kk)
            if np.isin(kk, hey):
                #if (key ==str):
                    #print("true")
                i, = np.where(hey == kk)
                i#f (key ==str):
                    #print(index)
                index = i[0]
                matrix[key][k][index] = vv #percent 0.03
                #if (key==str):
                    #print(matrix[key][k][index])
                    #print(matrix[key][k][0])






for key, value in matrix.items():
    print("\n")
    print("key: ",key)
    print("\n")
    for k, v in matrix[key].items():
        print( "cell:", k, " :", v)
