import java.util.ArrayList;
import java.util.Scanner;

public class ChatBot {
    public static void main(String[] args) {
        System.out.println("------------------------------------");
        System.out.println("Hello! I'm ChatBot!");
        System.out.println("What can I do for you?");
        System.out.println("------------------------------------");

        Scanner scanner = new Scanner(System.in);
        ArrayList<String> list = new ArrayList<>();
        while (true) {
            String input = scanner.nextLine();
            System.out.println("------------------------------------");
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("------------------------------------");
                break;
            } else if (input.equals("list")) {
                for (int i = 0; i < list.size(); i++) {
                    System.out.printf("%d. %s\n", i + 1, list.get(i));
                }
            } else {
                list.add(input);
                System.out.println("added: " + input);
            }
            System.out.println("------------------------------------");
        }
    }
}
