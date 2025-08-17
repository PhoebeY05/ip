import java.util.Scanner;

public class ChatBot {
    public static void main(String[] args) {
        System.out.println("------------------------------------");
        System.out.println("Hello! I'm ChatBot!");
        System.out.println("What can I do for you?");
        System.out.println("------------------------------------");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            System.out.println("------------------------------------");
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("------------------------------------");
                break;
            } else {
                System.out.println(input);
                System.out.println("------------------------------------");
            }
        }
    }
}
