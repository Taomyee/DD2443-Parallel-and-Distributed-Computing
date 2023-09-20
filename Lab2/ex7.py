import matplotlib.pyplot as plt
import numpy as np

threads = [2, 4, 8, 16, 32, 48, 96]
sequential_time = 154.10
sequential_std = 29.84
executor_service_time = [210.36, 161.11, 146.10, 117.15, 102.22, 100.91, 123.52]
executor_service_std = [3.22, 2.21, 8.85, 21.83, 24.03, 22.68, 24.29]

thread_time = [214.50,159.12,146.54,113.64,126.55,133.63,143.39]
thread_std = [3.06,2.04,8.34,26.92,29.35,30.71,31.68]

forkjoinpool_time = [192.37, 136.66, 79.42, 56.10, 44.26, 42.03,44.92]
forkjoinpool_std = [11.54, 6.32, 5.22, 3.10, 2.46, 2.48,3.39]

parallel_stream_time = [167.82,107.00,73.46,59.23,49.78,58.80,121.49]
parallel_stream_std = [27.67,12.90,14.57,18.01,9.36,13.91,29.73]

speedup_executor_service = [sequential_time / executor_service_time[i] for i in range(len(threads))]

plt.plot(threads, speedup_executor_service, label='ExecutorServiceSort')
plt.title("Speedup vs. Thread Count")
plt.xlabel("Number of Threads (n)")
plt.ylabel("Speedup")
plt.legend()
plt.grid(True)
plt.savefig('executorService.png')
plt.show()

speedup_thread = [sequential_time / thread_time[i] for i in range(len(threads))]
plt.plot(threads, speedup_thread, label='ThreadSort')
plt.title("Speedup vs. Thread Count")
plt.xlabel("Number of Threads (n)")
plt.ylabel("Speedup")
plt.legend()
plt.grid(True)
plt.savefig('thread.png')
plt.show()

speedup_forkjoinpool = [sequential_time / forkjoinpool_time[i] for i in range(len(threads))]
plt.plot(threads, speedup_forkjoinpool, label='ForkJoinPoolSort')
plt.title("Speedup vs. Thread Count")
plt.xlabel("Number of Threads (n)")
plt.ylabel("Speedup")
plt.legend()
plt.grid(True)
plt.savefig('ForkJoinPool.png')
plt.show()

speedup_parallel_stream = [sequential_time / parallel_stream_time[i] for i in range(len(threads))]
plt.plot(threads, speedup_parallel_stream, label='ParallelStreamSort')
plt.title("Speedup vs. Thread Count")
plt.xlabel("Number of Threads (n)")
plt.ylabel("Speedup")
plt.legend()
plt.grid(True)
plt.savefig('ParallelStream.png')
plt.show()
