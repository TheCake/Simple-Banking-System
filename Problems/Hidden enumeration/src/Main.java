public class Main {

    public static void main(String[] args) {
        int i = 0;
        for (Secret secret : Secret.values()) {
            if (secret.toString().startsWith("STAR")) {
                i++;
            }
        }
        System.out.println(i);
    }
}

/*
   enum Secret {
    STAR, CRASH, START, // ...
}
 */