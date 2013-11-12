CC=mpicc
CFLAGS= -O0 -g -o
PROCS= 2
ITERS=20
LIBPATH= #SET ME TO WHAT YOU DESIRE

compile: 
	$(CC) denoiser.c $(LIBPATH) $(CFLAGS) denoiser

run:
	mpirun -n $(PROCS) ./oblig1 0.5 $(ITERS) noisy-paprika.jpg out-pic.jpg

valgrind:
	valgrind mpirun -n $(PROCS) ./denoiser 0.5 $(ITERS) noisy-paprika.jpg out-pic.jpg

clean: 
	rm -rf *.o oblig1
