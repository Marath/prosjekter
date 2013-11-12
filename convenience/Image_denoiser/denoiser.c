#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <errno.h>
//#define DEBUG
//@author marath

/* 
   lowest index in block distribution 
   overlapp: BLOCK_LOW(id, p, n-2)
*/
#define BLOCK_LOW(id, p, n)			\
  ((id)*(n)/(p))

/* highest index */
#define BLOCK_HIGH(id, p, n)			\
  BLOCK_LOW((id)+1, (p), (n))

/* 
   size of block 
   overlapp BLOCK_SIZE(id, p, m-2) + 2 
*/
#define BLOCK_SIZE(id, p, n)					\
  (BLOCK_HIGH((id), (p), (n)) - BLOCK_LOW((id), (p), (n)))


typedef struct {
  float** image_data;   /* a 2D array of floats */
  int m;                /* # pixels in x-direction */
  int n;                /* # pixels in y-direction */
} image;

// make use of two functions from the simplejpeg library
void import_JPEG_file(const char *filename, unsigned char **image_chars,
		      int *image_height, int *image_width,
		      int *num_components);
void export_JPEG_file(const char *filename, unsigned char *image_chars,
		      int image_height, int image_width,
		      int num_components, int quality);

// allocate the array image_data inside u with m and n as input
void allocate_image(image *u, int m, int n) {
  int i, j;
  
  if(u == NULL) {
    perror("Struct u is NULL\n");
    printf("errno = %d.\n", errno);
  }
  
  u->image_data = (float**) malloc(m * sizeof(float**));
  u->m = m;
  u->n = n;

  for(i = 0; i<m; i++) {
    u->image_data[i] = malloc(n * sizeof(float));
  }
}

// deallocate the image_data inside u
void deallocate_image(image *u) {
  int i;
  if(u->image_data == NULL) {
    perror("u->image_data is NULL\n");
    printf("errno = %d.\n", errno);
  }
  for(i = 0; i < u->m; i++) {
    free(u->image_data[i]);
  }
  free(u->image_data);
}

void communicate(image *u, int myrank, int numprocs) {
  //starter med 2 prossesser
  //mer enn 2 prossesser: my_rank % num_procs
  //
  //   |          | <- block 1
  //   |          |
  //   |__________|
  //   |          | overlapp 
  //   |__________| <- block 2
  //   |          |
  //   |          |
  //   |__________|

  if(myrank == 0) {
    MPI_Send(u->image_data[u->n-1], u->m*2, MPI_FLOAT, 1, 0, MPI_COMM_WORLD);
    MPI_Recv(u->image_data[1], u->m*2, MPI_FLOAT, 0, 0, MPI_COMM_WORLD);
  } else {
    MPI_Send(u->image_data[1], u->m*2, MPI_FLOAT, 0, 0, MPI_COMM_WORLD);
    MPI_Recv(u->image_data[u->n-1], u->m*2, MPI_FLOAT, 1, 0, MPI_COMM_WORLD);
  }
}

//allocate U_bar before running this function
void iso_diffusion_denoising(image *u, image *u_bar, float kappa, int iters) {
  int i, j, k;
  for(k = 0; k<iters; k++) {

    for(i=1; i < (u->m-1); i++) {
      for(j=1; j < (u->n-1); j++) {
	u_bar->image_data[i][j] = 
	  u->image_data[i][j] + kappa*(u->image_data[i-1][j] + 
				       u->image_data[i][j-1] +
				       u->image_data[i+1][j] + 
				       u->image_data[i][j+1] -
				       4*u->image_data[i][j]);
      }
    }
    // boundary elements
    for (i = 0; i < u->m; i++) {
      u_bar->image_data[i][0] = u->image_data[i][0];
      u_bar->image_data[i][u->n-1] = u->image_data[i][u->n-1];
    }
    for (j = 0; j < u->n; j++) {
      u_bar->image_data[0][j] = u->image_data[0][j];
      u_bar->image_data[u->m-1][j] = u->image_data[u->m-1][j];
    }
    //copy back into u
    for(i = 0; i<u->m; i++) {
      for(j=0; j<u->n; j++) {
	u->image_data[i][j] = u_bar->image_data[i][j];
      }
    }
  } // end k-for
}


int main(int argc, char *argv[])
{
  /* declarations from precode */
  int m, n, c, iters;
  int my_m, my_n, my_rank, num_procs;
  float kappa;
  image u, u_bar;
  unsigned char *image_chars;
  char *input_jpeg_filename, *output_jpeg_filename;
  /* Decls made by me */
  //ints
  int i, j, t = 0;
  int b_low, b_high, b_size, part_size;
  int i_c_size, i_c_sizepart; //image_char size of sizepart
  float i_c_sizepart_f, offset;
  //pointers
  int *displs, *scount;
  int *recvbuf; //recieve buffer for gatherv

  //MPI
  MPI_Init (&argc, &argv);
  MPI_Comm_rank (MPI_COMM_WORLD, &my_rank);
  MPI_Comm_size (MPI_COMM_WORLD, &num_procs);
  /* read from command line: kappa, iters, input_jpeg_filename, output_jpeg_filename */
  if(argc != 5) {
    printf("need 4 arguments, given: %d\n", argc-1);
    return 0;
  }
  kappa = atof(argv[1]);
  iters = atoi(argv[2]);
  input_jpeg_filename = argv[3];
  output_jpeg_filename = argv[4];

  /* ... */

  if (my_rank==0) 
    import_JPEG_file(input_jpeg_filename, &image_chars, &m, &n, &c);
  
  MPI_Bcast (&m, 1, MPI_INT, 0, MPI_COMM_WORLD);
  MPI_Bcast (&n, 1, MPI_INT, 0, MPI_COMM_WORLD);
 
  /* divide the m x n pixels evenly among the MPI processes 
  
     -----m----->
     |__________||
     |__________|n
     |__________||
     |__________|v 
  */
  
  // printf("i_c_size: %d, i_c_sizepart: %d\n", i_c_size, i_c_sizepart);  

  //blocks from macros
  b_low = m * BLOCK_LOW(my_rank, num_procs, n);
  b_high = m * BLOCK_HIGH(my_rank, num_procs, n);
  b_size = BLOCK_SIZE(my_rank, num_procs, n-2) + 2;

  //my_m, my_n
  my_m = m; 
  my_n = b_size; //evt b_size / m
  unsigned char partition[my_m*my_n]; //array partition

  //alloker plass til numprocs - if root process
  if(my_rank == 0) {
    scount = (int *) malloc(m * sizeof(int));
    displs = (int *) malloc(m * sizeof(int));
    //partition = (char *) malloc(b_high * sizeof(char));
  } // END root proc if
  
  /* scounts and displs are made here */
  for(i=0;i<num_procs; i++) {
    scount[i] = (BLOCK_SIZE(i, num_procs, n-2) +2) * n;
    displs[i] = BLOCK_LOW(i, num_procs, n-2) * n;
  }

#ifdef DEBUG
  if(my_rank == 0) {
    printf("contents of displs: \n");
    for(i = 0; i < num_procs; i++) {
      printf("%d ", displs[i]);
    }
    printf("\n");
    
    printf("contents of scounts: \n");
    for(i = 0; i < num_procs; i++) {
      printf("%d ", scount[i]);
    }
    printf("\n");
  }
#endif
  
  
#ifdef DEBUG
  printf("my_m: %d, my_n: %d, size of partition array: %d\n", my_m, my_n, my_m*my_n);
#endif
  
  MPI_Scatterv(image_chars, scount, displs, MPI_UNSIGNED_CHAR, partition,
    b_high, 
    MPI_UNSIGNED_CHAR, 0, MPI_COMM_WORLD);

  printf("Scatterv done in process: %d\n", my_rank);
  
  allocate_image (&u, my_m, my_n);
  allocate_image (&u_bar, my_m, my_n);

  /* each process asks process 0 for a partitioned region */
  /* of image_chars and copy the values into u */
  /* ... */
 
#ifdef DEBUG
  printf("u.m = %d, u.n = %d, u_bar.m = %d, u_bar.n = %d, in process: %d\n", 
    u.m, u.n, u_bar.m, u_bar.n, my_rank);
#endif  

  for(i=0; i<my_m; i++) {
    for(j=0; j<my_n; j++) {
      u.image_data[i][j] = (float) image_chars[i*my_m + j]; //maybe my_n
      
    }
  }
  
  printf("Array flipped to 2D in process: %d\n", my_rank);

  //each process does this 
  iso_diffusion_denoising (&u, &u_bar, kappa, iters);
  printf("Denoising done in process: %d\n", my_rank);

  /* each process sends its resulting content of u_bar to process 0 */
  /* process 0 receives from each process incoming values and */
  /* copy them into the designated region of image_chars */
  /* ... */


  for(i=0; i<my_m; i++) {
    for(j=0; j<my_n; j++) {
      image_chars[i*my_m + j] = u.image_data[i][j];
    }
  }
  printf("Array flipped to 1D in process: %d\n", my_rank);
  
  MPI_Gatherv(partition, b_high, MPI_UNSIGNED_CHAR, image_chars, 
    scount, displs, MPI_UNSIGNED_CHAR, 0, MPI_COMM_WORLD);
  printf("Gatherv done in process: %d\n", my_rank);
  
  if (my_rank==0)
    export_JPEG_file(output_jpeg_filename, image_chars, m, n, c, 75);
  
  deallocate_image (&u);
  deallocate_image (&u_bar);
  MPI_Finalize();
  
  printf("GREAT SUCCESS!!!\n");
  return 0;
}
