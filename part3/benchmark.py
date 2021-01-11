from functools import wraps
from statistics import mean, variance
from threading import Thread
import csv
import time

def benchmark(warmups=0, iter=1, verbose=False, csv_file=None):
    def inner_function(func):
        @wraps(func)
        def wrapper_benchmark(*args, **kwargs):
            if warmups:
                times = []
                print("\n--------------WARMUPS PHASE--------------")
                print("Warmup\t\t| Time needed")
                for i in range(warmups):
                    start_time = time.perf_counter()
                    func(*args, **kwargs)
                    end_time = time.perf_counter()

                    if verbose:
                        res = end_time - start_time
                        times.append([i + 1, True, res])
                        print(f"{i + 1}\t\t| {res}")

                if verbose:
                    print("*****************************************")
                    collapsed_times = [t[2] for t in times]
                    average = mean(collapsed_times)
                    var = variance(collapsed_times)
                    total = sum(collapsed_times)
                    print(f"Total time\t| {total}")
                    print (f"Average time\t| {average}")
                    if len(times) > 1: print (f"Variance\t| {var}")
                    print("*****************************************")
                    if (csv_file):
                        to_csv(csv_file, times, total, average, var)

            print("\n----------------ITER PHASE---------------")
            print("Iter\t\t| Time needed")
            times = []
            for i in range(iter):
                start_time = time.perf_counter()
                func(*args, **kwargs)
                end_time = time.perf_counter()

                res = end_time - start_time
                times.append((i + 1, False, res))
                print(f"{i + 1 }\t\t| {res}")

            print("*****************************************")
            collapsed_times = [t[2] for t in times]
            average = mean(collapsed_times)
            var = variance(collapsed_times)
            total = sum(collapsed_times)
            print(f"Total time\t| {total}")
            print (f"Average time\t| {average}")
            if len(times) > 1: print (f"Variance\t| {var}")
            print("*****************************************")

            if (csv_file):
                to_csv(csv_file, times, total, average, var)

            
        return wrapper_benchmark
    return inner_function


#@benchmark(warmups=2, iter=5, verbose=True, csv_file="benchfibo.csv")
def fibonacci(n=31):
    def fibo(n):
        if n == 0:
            return 0
        elif n == 1:
            return 1
        else:
            return fibo(n-1) + fibo(n-2)

    fibo(n)


def test(f, *args, **kwargs):
    def test_f(nthreads=1, ntimes=1):
        @benchmark(iter=ntimes, verbose=True, csv_file=f"{f.__name__}_{nthreads}_{ntimes}.csv")
        def wrapper():
            f(*args, **kwargs)

        pool = [
            Thread(target=wrapper) for _ in range(nthreads)
        ]
        
        for t in pool:
            t.start()
        for t in pool:
            t.join()

    test_f(1, 16)
    test_f(2, 8)
    test_f(4, 4)
    test_f(8, 2)


def to_csv(csv_file, results, total, average, variance):
    with open(csv_file, 'a') as result_file:
        writer = csv.writer(result_file, quoting=csv.QUOTE_MINIMAL)
        for t in results:
            writer.writerow(t)

        writer.writerow(["total", total])
        writer.writerow(["average", average])
        writer.writerow(["variance", variance])


if __name__ == "__main__":
    #fibonacci()
    test(fibonacci)


""" 
Due to the Global Interpreter Lock, only one thread at a time can access the
interpreter in order to execute Python bytecode. Also, considering that the task
executed by the threads is a CPU-bound task, the GIL cannot be released as in
the case of an I/O-bound task. This situation is reflected in the Fibonacci
function result where the iterations of the function executed by 2 threads take
almost double the time that is needed by an iteration done in a single thread
context.
"""

