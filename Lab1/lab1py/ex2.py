import matplotlib.pyplot as plt

n = [1,2,4,8,16,32,64]
avgExeTime = [368601,310499,302824,246075,274425,251434,241892]
stdDeviation = [0.0,50899.5,54642.4,62086.8,59383.8,42540.0,47199.9]
plt.plot(n, avgExeTime, 'ro')
plt.errorbar(n, avgExeTime, yerr=stdDeviation, fmt='o')
plt.xlabel('Number of threads')
plt.ylabel('Average execution time (ns)')
plt.title('Average execution time vs. number of threads')
plt.savefig('ex2.png')
plt.show()