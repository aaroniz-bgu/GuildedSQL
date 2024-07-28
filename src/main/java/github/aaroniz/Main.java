package github.aaroniz;

import github.aaroniz.api.GuildedSQL;
import github.aaroniz.api.GuildedSQLBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
            var server = "YOUR_SERVER_ID";
            var token = "YOUR_ACCESS_API_TOKEN";
            var api = new GuildedSQLBuilder()
                    .setServerId(server)
                    .isPrivate(true)
                    .token(token)
                    .build();

            try(Scanner scan = new Scanner(System.in)) {
                String input;
                boolean running = true;
                System.out.println("ready!");
                while(running) {
                    input = scan.nextLine();
                    if(input.equals("exit")) {
                        running = false;
                    } else {
                        int qInd = input.indexOf("`");
                        if(qInd == -1) {
                            parseExec(input.trim().split(" "), api);
                        } else {
                            String value = input.substring(qInd + 1);
                            qInd = value.indexOf("`");
                            if(qInd == -1) {
                                illegalInput("closing ` ");
                            } else {
                                value = value.substring(0, qInd);
                                input = input.substring(0, input.indexOf("`"));
                                String[] inputSplit = input.trim().split(" ");
                                String[] combined = Arrays.copyOf(inputSplit, inputSplit.length + 1);
                                combined[inputSplit.length] = value;
                                parseExec(combined, api);
                            }
                        }
                    }
                }
            }
    }

    private static void parseExec(String[] input, GuildedSQL api) {
        try {
            if (input.length > 0) {
                if (input[0].equals("create-table")) {
                    if(input.length == 2) {
                        var res = api.createTable(input[1]);
                        System.out.println("Created table: "+res.getName());
                        System.out.println("Channel: "+res.getUUID());
                    } else if (input.length == 3) {
                        var res = api.createTable(input[1],input[2]);
                        System.out.println("Created table: "+res.getName());
                        System.out.println("Channel: "+res.getUUID());
                    } else illegalInput("<table-name> <table-desc>? for create-table");
                } else if (input[0].equals("drop-table")) {
                    if(input.length == 2) {
                        if (api.deleteTable(input[1]))
                        System.out.println("Table deleted.");
                        else System.out.println("Deletion failed.");
                    } else illegalInput("<table-name> for drop-table");
                } else if (input[0].equals("insert")) {
                    if(input.length == 5 && input[1].equals("to")) {
                        api.insert(input[2], input[3], input[4]);
                        System.out.println("Operation completed.");
                    } else illegalInput("\"to\" <table-name> <unique-key> <data> for insert");
                } else if (input[0].equals("get")) {
                    if(input.length > 1 && input[1].equals("table")) {
                        if(input.length == 3) {
                            var res = api.get(input[2]);
                            for(var i : res) {
                                System.out.println(i);
                            }
                        } else if (input.length == 4) {
                            var res = api.get(input[2], Integer.parseInt(input[3]));
                            for(var i : res) {
                                System.out.println(i);
                            }
                        } else illegalInput(null);
                    } else if (input.length == 4 && input[1].equals("from")) {
                        var res = api.get(input[2], input[3]);
                        System.out.println(res);
                    } else illegalInput("\"from\" <table-name> <unique-key> | \"table\" <table-name> <int>? for get");
                } else if (input[0].equals("contains")) {
                    if(input.length == 4 && input[2].equals("in")) {
                        System.out.println(api.contains(input[3], input[1]));
                    } else illegalInput("<unique-key> \"in\" <table-name> for contains");
                } else if (input[0].equals("delete")) {
                    if(input.length == 4 && input[2].equals("in")) {
                        if(api.delete(input[3], input[1])) {
                            System.out.println("Entry deleted.");
                        } else System.out.println("Deletion failed.");
                    } else illegalInput("<unique-key> \"in\" <table-name> for delete");
                }
            } else illegalInput("<guilded-sql func> := create-table | drop-table | insert | get | contains | delete");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void illegalInput(String msg) {
        System.out.println("Illegal input");
        if(msg != null) System.out.println("Expected: "+msg);
    }
}