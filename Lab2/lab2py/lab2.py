import matplotlib.pyplot as plt
import numpy as np

# Efficiency factor (e)
e = 0.1

# Parallelizable part (p) values
p_values = [0.2, 0.4, 0.6, 0.8]

# Thread counts (n)
thread_counts = [2, 4, 8, 16]

# Initialize a dictionary to store results
speedup_results = {}

# Calculate speedup for each combination of p and n
for p in p_values:
    speedup_values = []
    for n in thread_counts:
        speedup = 1 / ((1 - p) + (p / n) + e)
        speedup_values.append(speedup)
    speedup_results[p] = speedup_values

# Plot the speedup results
plt.figure(figsize=(10, 6))
for p, speedups in speedup_results.items():
    plt.plot(thread_counts, speedups, label=f'p={p}')

plt.title("Speedup vs. Thread Count for Different Values of p")
plt.xlabel("Number of Threads (n)")
plt.ylabel("Speedup")
plt.legend()
plt.grid(True)
plt.savefig('speedup.png')
plt.show()
