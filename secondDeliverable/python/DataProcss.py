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

#print(glori[str].items())

# for key, value in glori.items():
#     print("\n")
#     print("key: ",key)
#     print("\n")
#     for k, v in glori[key].items():
#         print( "cell:", k, " :", v)

#now I have glori but it should have a fixed format, I am going to consider values from -38 to -94
hey = np.arange(-38, -95, -1) #reference, future will be np.arange(0, 255, 1)
row = np.zeros(len(hey))
reference = np.arange(16, 0, -1)




matrix = {}



for key, value in glori.items():
    matrix[key] = {}
    for k, v in glori[key].items():
        matrix[key][k]={}
        for i in range(len(row)):
            matrix[key][k][i] = 0

# for key, value in matrix.items():
#     print("\n")
#     print("key: ",key)
#     print("\n")
#     for k, v in matrix[key].items():
#         print( "cell:", k, " :", v)


count = 0
for key, value in glori.items():
    for j in range(len(reference)):
        if reference[j] in glori[key].keys():
            for kk, vv in glori[key][reference[j]].GetDict().items():
                if np.isin(kk, hey):
                    i, = np.where(hey == kk)
                    index = i[0]
                    matrix[key][reference[j]][index] = vv  # percent 0.03
        else:
            matrix[key][reference[j]] = {}
            for i in range(len(row)):
                matrix[key][reference[j]][i] = 0  # stuff non found cells w zeros

    # for k, v in glori[key].items():
    #     for kk, vv in glori[key][k].GetDict().items():
    #         if np.isin(kk, hey):
    #             i, = np.where(hey == kk)
    #             index = i[0]
    #             matrix[key][k][index] = vv #percent 0.03


# we introduce zeros NEWSTUFF
# for key, value in glori.items():
#     for j in range(len(reference)):
#         if j in glori[key]:
#             continue
#         else:
#             matrix[key][j] = {}
#             for i in range(len(row)):
#                 matrix[key][j][i] = 0 #stuff non found cells w zeros



# for key, value in matrix.items():
#     print("\n")
#     print("key: ",key)
#     print("\n")
#     for k, v in matrix[key].items():
#         print( "cell:", k)
#         for kk, vv in matrix[key][k].items():
#             print(-kk-38, vv)


#lets try to order dict
definitive = {}

for key, value in matrix.items():
    definitive[key] = {}
    definitive[key] = OrderedDict(sorted(matrix[key].items(), key=lambda t: t[0]))


for key, value in matrix.items():
    trial = OrderedDict(matrix[key])
    print(trial)

file1 = open("MyFile.txt","a")
for key, value in definitive.items():
    file1.write("\n")
    file1.write("key: ")
    file1.write(key)
    for k, v in definitive[key].items():
        file1.write("\n")
        file1.write( "cell: ")

        file1.write(str(k))
        file1.write("\n")
        for kk, vv in definitive[key][k].items():
            file1.write(str(vv))
            file1.write(" ,")

    file1.write("\n")

file1.close()