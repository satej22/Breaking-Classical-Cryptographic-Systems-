import java.math.BigInteger;
import java.util.*;
//import mpi.MPI;
//import mpi.MPIException;

// Increase the sieve limit to get more smooth values

public class QuadraticSeive {
    BigInteger n ;
    BigInteger rootn ;
    int bsmooth;

    Integer[] primes;

    Integer[] factor_base ;

    BigInteger[] Q_list , origional_q_list;

    ArrayList<Integer> smoothx = new ArrayList<>();

    int currlist = 0;

    ArrayList<ArrayList<Integer>> VectorList = new ArrayList<>();

    ArrayList<ArrayList<Integer>> idxvectorlist ;
    ArrayList<ArrayList<Integer>> final_x_values = new ArrayList<>(); ;

    // sieve limit for 30 digit 4000000 ( 4M )
    public int SEIVE_LIMIT ;
    int rank =0,size=0 ;
    BigInteger x , y;

    public QuadraticSeive(BigInteger n, BigInteger rootn, int bsmooth , int SEIVE_LIMIT) {
        this.n = n;
        this.rootn = rootn;
        this.bsmooth = bsmooth;

        if(rootn.multiply(rootn).equals(n)){
            Global.print("Factors are "+ rootn.toString() + " and "+ rootn.toString());
            return;
        }

        // Get rank (unique process ID) and size (total number of processes)
//        this.rank = MPI.COMM_WORLD.Rank();
//        this.size = MPI.COMM_WORLD.Size();

        this.rank = 0;
        this.size = 1;

        this.primes = createPrimes(2 , bsmooth);
        this.SEIVE_LIMIT = SEIVE_LIMIT;

        this.factor_base = set_factor_base();


//        this.Q_list = find_all_Q();
        this.Q_list = find_partial_Q();


        if (rank == 0) {

            DivideByPrime();
            Global.print("Divide by Prime Done");
            lookforone();

            this.Q_list = null;

            this.origional_q_list = find_all_Q();

            Global.print("Look for one done");

            VectorCreation();

            Global.print("Size of the vectorlist :"+VectorList.size());

            this.idxvectorlist = gaussianElimination(VectorList);

            Global.print("Gaussian elemination done ");

            VectorList = null;

            System.out.println("---------------------------------------------------------");

            for(ArrayList<Integer> i:idxvectorlist){
                ArrayList<Integer> res = new ArrayList<>();
                for(int x:i){
                    res.add(smoothx.get(x));
                }
                final_x_values.add(res);
            }

//            System.out.println(" List of x such that the vectors are dependent -> ");

            this.x = computex(currlist,final_x_values);
            this.y = computey(currlist,final_x_values);

            while(x.equals(y)){
                this.currlist += 1;

                this.x = computex(this.currlist, final_x_values);
                this.y = computey(this.currlist, final_x_values);
            }
            BigInteger v = x.subtract(y);
            BigInteger first = v.gcd(n);
            BigInteger second = n.divide(first);


            System.out.println("Factors of "+n+" are : "+first+ " ,  "+ second);
            System.out.println("---------------------------------------------------------");


        }







    }
    public BigInteger[] find_partial_Q() {
        BigInteger[] Q_list = new BigInteger[SEIVE_LIMIT / size];
        int offset = rank * (SEIVE_LIMIT / size);

        // Each process computes its portion of Q_list
        for (int i = 0; i < SEIVE_LIMIT / size; i++) {
            Q_list[i] = Q(i + offset);
        }

        // You may want to gather the full Q_list at process 0, depending on your logic
        return Q_list;
    }




    public BigInteger Q(int x){
        BigInteger e1 = new BigInteger(String.valueOf(x)).add(rootn).pow(2);
        return e1.subtract(n);
    }



    public BigInteger[] find_all_Q(){
        BigInteger[] Q_list = new BigInteger[SEIVE_LIMIT];

        for (int i = 0; i < SEIVE_LIMIT; i++) {
            Q_list[i] = Q(i);
        }

        return Q_list;
    }



    public Integer[] set_factor_base(){
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for (Integer pt : primes) {
            if(getLegendarySymbol(pt) == 1){
                integerArrayList.add(pt);
            }
        }

        return integerArrayList.toArray(new Integer[0]);

    }

    public static Integer[] createPrimes(int from, int to) {
        if (to < 2) {
            return new Integer[0]; // No primes less than 2
        }

        boolean[] isPrime = new boolean[to + 1];
        for (int i = 2; i <= to; i++) {
            isPrime[i] = true;
        }

        for (int i = 2; i * i <= to; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= to; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int i = Math.max(2, from); i <= to; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }

        return primes.toArray(new Integer[0]);
    }

    public int getLegendarySymbol(int prime) {
        if (prime < 2) {
            throw new IllegalArgumentException("Prime must be greater than 2.");
        }
        if (prime == 2) {
            return isMod0(n,prime) ? 0 : 1;
        }
        if (isMod0(n,prime)) {
            return 0;
        }
        int exponent = (prime - 1) / 2;
        BigInteger result = n.mod(BigInteger.valueOf(prime)).modPow(BigInteger.valueOf(exponent), BigInteger.valueOf(prime));

        if (result.equals(BigInteger.valueOf(prime - 1))) {
            return -1;
        } else {
            return result.intValue();
        }

    }

    public boolean isMod0(BigInteger n , int prime){
        return n.mod(BigInteger.valueOf(prime)).equals(new BigInteger("0"));
    }


    public static BigInteger sqrtMod(BigInteger a, BigInteger p) {

        if (p.equals(BigInteger.valueOf(2))) {
            if (a.mod(p).equals(BigInteger.ZERO)) {
                return BigInteger.ZERO; // sqrt(0) mod 2
            } else {
                return BigInteger.ONE; // sqrt(1) mod 2
            }
        }
        // Check if a is a quadratic residue modulo p
        if (a.modPow(p.subtract(BigInteger.ONE).divide(BigInteger.TWO), p).equals(BigInteger.ONE)) {
            // Find s and q such that p - 1 = q * 2^s
            BigInteger s = BigInteger.ZERO;
            BigInteger q = p.subtract(BigInteger.ONE);
            while (q.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                q = q.divide(BigInteger.TWO);
                s = s.add(BigInteger.ONE);
            }

            // Find a quadratic non-residue z modulo p
            BigInteger z;
            for (z = BigInteger.valueOf(2); z.modPow(p.subtract(BigInteger.ONE).divide(BigInteger.TWO), p).equals(BigInteger.ONE); z = z.add(BigInteger.ONE)) {
            }

            // Compute the variables used in the algorithm
            BigInteger m = s;
            BigInteger c = z.modPow(q, p);
            BigInteger r = a.modPow(q.add(BigInteger.ONE).divide(BigInteger.TWO), p);
            BigInteger t = a.modPow(q, p);
            BigInteger t2i = t;

            // Loop until t is zero or one
            while (!t.equals(BigInteger.ZERO) && !t.equals(BigInteger.ONE)) {
                // Find the least i such that t^(2^i) ≡ 1 (mod p)
                BigInteger i;
                for (i = BigInteger.ONE; i.compareTo(m) < 0; i = i.add(BigInteger.ONE)) {
                    t2i = t.modPow(BigInteger.TWO.pow(i.intValue()), p);
                    if (t2i.equals(BigInteger.ONE)) {
                        break;
                    }
                }

                // Compute the new variables
                BigInteger b = c.modPow(BigInteger.TWO.pow(m.subtract(i).subtract(BigInteger.ONE).intValue()), p);
                m = i;
                c = b.modPow(BigInteger.TWO, p);
                r = r.multiply(b).mod(p);
                t = t.multiply(c).mod(p);
            }

            return r;
        } else {
            // No square root exists
            throw new IllegalArgumentException("No square root exists for the given value modulo " + p);
        }

    }

    public static BigInteger modInverse(BigInteger a, BigInteger p) {
        return a.modInverse(p);
    }

    public static Object[] solve_quadratic_mod(BigInteger b , BigInteger c , BigInteger p){
        BigInteger D = b.pow(2).subtract(c.multiply(BigInteger.valueOf(4)));
        BigInteger sqrt_d = sqrtMod(D,p);
        BigInteger inv_2a = modInverse(BigInteger.valueOf(2),p);

        BigInteger minus_b = b.multiply(BigInteger.valueOf(-1));
        BigInteger ans1 = minus_b.add(sqrt_d);
        ans1 = ans1.multiply(inv_2a);
        ans1 = ans1.mod(p);

        BigInteger ans2 = minus_b.subtract(sqrt_d);
        ans2 = ans2.multiply(inv_2a);
        ans2 = ans2.mod(p);

// System.out.println("x1 : "+ans1);
// System.out.println("x2 : "+ans2);
        return new Object[]{ans1,ans2};
    }

    public Object[] qx_sqrt(BigInteger p){
        BigInteger b = rootn.multiply(BigInteger.valueOf(2));
        b = b.mod(p);

        BigInteger c = rootn.multiply(rootn);
        c = c.subtract(n);
        c = c.mod(p);

        return solve_quadratic_mod(b,c,p);
    }


    private void DivideByPrime(){
        for(int p=0;p< factor_base.length;p++){
            BigInteger prime_big = BigInteger.valueOf(factor_base[p]);
            Object[] x = {BigInteger.ZERO, BigInteger.ZERO};
            if(factor_base[p]!=2) {
                x = qx_sqrt(prime_big);
            }
            BigInteger x1b = (BigInteger) x[0];
            BigInteger x2b = (BigInteger) x[1];

            int x1 = x1b.intValue();
            int x2 = x2b.intValue();
            int prime = factor_base[p];
// System.out.println("Prime : "+prime+"------------------------------>");
// System.out.println("X1 : "+x1 + " X2 :"+x2);



            int k = 0;
            while(((x1+(k * prime)) < Q_list.length)){
                int power = 0;
                BigInteger result = prime_big.pow(power+1);
                while(Q_list[x1+(k*prime)].mod(result).equals(BigInteger.ZERO)){
                    power++;
                    result = prime_big.pow(power+1);
                }
// System.out.println(Q_list[x1+(k*prime)]+" is divided by "+prime+" to the Power :"+power);
                Q_list[x1+(k*prime)] = Q_list[x1+(k*prime)].divide(prime_big.pow(power));

// if(Q_list[x1+(k*prime)].divide(prime_big.pow(power)).equals(BigInteger.ZERO)){
// System.out.println(" Reason for 0 value :");
// System.out.println("Prime : "+prime+"------------------------------>");
// System.out.println("X1 : "+x1 + " X2 :"+x2);
// System.out.println(Q_list[x1+(k*prime)]+" is divided by "+prime+" to the Power :"+power);
// }
                k++;
            }

            if(x1!=x2){
                int l = 0;
                while(((x2+(l * prime)) < Q_list.length)){
                    int power = 1;
                    BigInteger result = prime_big.pow(power+1);
                    while(Q_list[x2+(l*prime)].mod(result).equals(BigInteger.ZERO)){
                        power++;
                        result = prime_big.pow(power+1);
                    }
                    Q_list[x2+(l*prime)] = Q_list[x2+(l*prime)].divide(prime_big.pow(power));
// System.out.println("I am here ");
                    l++;
                }
            }


        }

    }

    private void lookforone(){
        // Build smoothx

        int count =0;
        for(int i=0;i<Q_list.length;i++){

            if(count < factor_base.length+200 ){
                if(Q_list[i].equals(BigInteger.ONE)){
                    smoothx.add(i);
                    count++;
                }
            }
        }

        System.out.println(" SMOOTH INDEX ");
        System.out.println(" NUMBER OF SMOOTH VALUES FOUND : "+smoothx.size());
        System.out.println(" SIZE OF FACTOR BASE : "+factor_base.length);

    }

    private void VectorCreation(){

        //Smoothx contains the x values for which prime factors of Q(x) is inside our factorbase itself

//        System.out.println(" Vectors : -------------------->>>>>>>");

        for(int i=0;i<smoothx.size();i++){
            ArrayList<Integer> vec = new ArrayList<>();
            BigInteger val = origional_q_list[smoothx.get(i)];

// System.out.println("Value : "+val + " ==================================>>>>>>>>>>>>>>");
            for(int j=0;j<factor_base.length;j++){
                int p = factor_base[j];
                int pow = 0;
                //Math.pow(p,pow+1))
                while(val.mod(((BigInteger.valueOf(p)).pow(pow+1))).equals(BigInteger.ZERO)){
                    pow++;
                }


                if(pow%2==1){
                    vec.add(1);
                }
                else{
                    vec.add(0);
                }

                val = val.divide(((BigInteger.valueOf(p)).pow(pow)));
            }
            VectorList.add(vec);
        }
    }

    // Gaussian elimination Part --->

    // Function to perform Gaussian elimination over GF(2) to find zero vector combination
    public static ArrayList<ArrayList<Integer>> gaussianElimination(ArrayList<ArrayList<Integer>> vectors) {
        int numRows = vectors.size();
        int numCols = vectors.get(0).size();

        // Matrix to track row combinations
        ArrayList<ArrayList<Integer>> combinations = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            // Initialize identity matrix where each row is its own combination initially
            ArrayList<Integer> comb = new ArrayList<>();
            for (int j = 0; j < numRows; j++) {
                comb.add(i == j ? 1 : 0); // Diagonal = 1, others = 0
            }
            combinations.add(comb);
        }

        // Perform Gaussian elimination
        ArrayList<Integer> pivotRows = new ArrayList<>();
        for (int col = 0, row = 0; col < numCols && row < numRows; col++) {
            // Step 1: Find pivot in this column
            int pivot = -1;

            //look for the pivot in the current column
            for (int i = row; i < numRows; i++) {
                if (vectors.get(i).get(col) == 1) {
                    pivot = i;
                    break;
                }
            }

            // If no pivot found, continue to next column
            if (pivot == -1) continue;

            // Step 2: Swap the current row with the pivot row
            swapRows(vectors, row, pivot);
            swapRows(combinations, row, pivot); // Swap in the combinations matrix as well
            pivotRows.add(row); // Record the pivot row

            // Step 3: Eliminate all entries below the pivot
            for (int i = row + 1; i < numRows; i++) {
                if (vectors.get(i).get(col) == 1) {
                    for (int j = col; j < numCols; j++) {
                        vectors.get(i).set(j, (vectors.get(i).get(j) + vectors.get(row).get(j)) % 2); // XOR the rows
                    }
                    // Track the combination of rows
                    for (int k = 0; k < numRows; k++) {
                        combinations.get(i).set(k, (combinations.get(i).get(k) + combinations.get(row).get(k)) % 2);
                    }
                }
            }
            row++;
        }

        // Step 6: Identify rows that contribute to zero vector
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            // Check if the current row forms a zero vector
            boolean isZero = true;
            for (int j = 0; j < numCols; j++) {
                if (vectors.get(i).get(j) != 0) {
                    isZero = false;
                    break;
                }
            }

            // If the row is zero, find the combination of vectors that formed it
            if (isZero) {
                ArrayList<Integer> res = new ArrayList<>();
                for (int k = 0; k < numRows; k++) {
                    if (combinations.get(i).get(k) == 1) {
                        res.add(k); // Add indices that contributed to the zero vector
// resultIndices.add(k); // Add indices that contributed to the zero vector
                    }

                }
                result.add(res);

            }
        }
        return result;
    }

    // Function to swap two rows in a matrix
    private static void swapRows(ArrayList<ArrayList<Integer>> matrix, int row1, int row2) {
        ArrayList<Integer> temp = matrix.get(row1);
        matrix.set(row1, matrix.get(row2));
        matrix.set(row2, temp);
    }

    private BigInteger computex(int listno,ArrayList<ArrayList<Integer>> zerosumlist){
        ArrayList<Integer> arr = zerosumlist.get(listno);
        BigInteger finalval = BigInteger.ONE;

        for(int i: arr){
            BigInteger val = rootn.add(BigInteger.valueOf(i));
            finalval = finalval.multiply(val);
            finalval = finalval.mod(n);
        }

        return finalval;
    }

    private BigInteger computey(int listno,ArrayList<ArrayList<Integer>> zerosumlist){
        BigInteger finalval = BigInteger.ONE;

        for(int i : zerosumlist.get(listno)){
            finalval = finalval.multiply(origional_q_list[i]);
        }

        finalval = finalval.sqrt();
        finalval = finalval.mod(n);

        return finalval;
    }

}
