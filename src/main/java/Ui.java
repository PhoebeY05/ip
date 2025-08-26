import java.util.ArrayList;

public class Ui {

    public boolean handleInput(Parser parser, ArrayList<Task> tasks) throws ChatBotException{

        System.out.println("------------------------------------");
        CommandType commandType = parser.getCommandType();
        Task addedTask = null;
        ArrayList<String> args = parser.getArguments();

        switch (commandType) {
            case BYE:
                this.endConversation();
                return false;
            case LIST:
                this.listTasks(tasks);
                break;
            case MARK:
                Task tDone = parser.getTask(tasks);
                tDone.markAsDone();
                this.showMarkedAsDone(tDone);
                break;
            case UNMARK:
                Task tUndone = parser.getTask(tasks);
                tUndone.markAsUndone();
                this.showMarkedAsUndone(tUndone);
                break;
            case DELETE:
                Task tDeleted = parser.getTask(tasks);
                tasks.remove(tDeleted);
                this.showDeleted(tDeleted, tasks.size());
                break;
            case TODO:
                System.out.println("Got it. I've added this task:");
                addedTask = new Todo(args.get(0));
                break;
            case DEADLINE:
                addedTask = new Deadline(args.get(0), args.get(1));
                break;
            case EVENT:
                addedTask = new Event(args.get(0), args.get(1), args.get(2));
                break;
            default:
                throw new ChatBotException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }

        if (addedTask != null) {
            tasks.add(addedTask);
            this.showAddedTask(addedTask, tasks.size());
        }

        System.out.println("------------------------------------");
        return true;
    }

    public void endConversation() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("------------------------------------");
    }

    public void listTasks(ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            Task curr = tasks.get(i);
            System.out.printf("%d.%s\n", i + 1, curr.toString());
        }
    }

    public void showMarkedAsDone(Task t) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(t);
    }

    public void showMarkedAsUndone(Task t) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(t);
    }

    public void showDeleted(Task t, int size) {
        System.out.println("Noted. I've removed this task:");
        System.out.println(t);
        System.out.printf("Now you have %d tasks in the list.\n", size);
    }

    public void showAddedTask(Task t, int size) {
        System.out.println(t);
        System.out.printf("Now you have %d tasks in the list.\n", size);
    }

    public void showLoadingError() {
        System.out.println("Error");
    }
}
