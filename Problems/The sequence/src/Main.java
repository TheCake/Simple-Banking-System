import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int i = 1;
        int j = 0;
        while (i <= n) {
            j++;
            for (int c = j; c > 0; c--) {
                if (i <= n) {
                    System.out.print(j);
                    i++;
                    if (i <= n) {
                        System.out.print(" ");
                    }
                }
            }
        }
    }
}