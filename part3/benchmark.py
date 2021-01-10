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
                    print (f"Average time\t| {mean(collapsed_times)}")
                    if len(times) > 1: print (f"Variance\t| {variance(collapsed_times)}")
                    print("*****************************************")
                    if (csv_file):
                        to_csv(csv_file, times)

            print("\n----------------ITER PHASE---------------")
            print("Iter\t\t| Time needed")
            average_time = 0
            times = []
            for i in range(iter):
                start_time = time.perf_counter()
                func(*args, **kwargs)
                end_time = time.perf_counter()

                res = end_time - start_time
                times.append((i + 1, False, res))
                average_time += res 
                print(f"{i + 1 }\t\t| {res}")

            print("*****************************************")
            collapsed_times = [t[2] for t in times]
            print (f"Average time\t| {mean(collapsed_times)}")
            if len(times) > 1: print (f"Variance\t| {variance(collapsed_times)}")
            print("*****************************************")

            if (csv_file):
                to_csv(csv_file, times)

            
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


def to_csv(csv_file, results):
    with open(csv_file, 'a') as result_file:
        writer = csv.writer(result_file, quoting=csv.QUOTE_MINIMAL)
        for t in results:
            writer.writerow(t)


if __name__ == "__main__":
    #fibonacci()
    test(fibonacci)

