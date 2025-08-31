

import java.math.BigInteger;

//import mpi.MPI;



public class Main {





    public static void main(String[] args) {
        Global.print("Execution Started");
        BigInteger n = new BigInteger("165050667374457919300349");
        BigInteger rootn = n.sqrt();

//        args = new String[]{"-np", "4", "-dev", "multicore"};
//        MPI.Init(args);


        int bsmooth = 50000;
        int SEIVE_LIMIT = 2000000;
        QuadraticSeive seive = new QuadraticSeive(n,rootn,bsmooth, SEIVE_LIMIT);


//        MPI.Finalize();

        Global.print("Execution Ended");
    }





}

