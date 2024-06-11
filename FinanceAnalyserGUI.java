import java.awt.CardLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class FinanceAnalyserGUI extends JFrame {
    private final Finance_Analyser app = new Finance_Analyser();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    
    public FinanceAnalyserGUI() {
        setTitle("Finance Analyser");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel menuPanel = new JPanel(new GridLayout(6, 1));
        
        JButton addPersonalExpenseButton = new JButton("Add Personal Expense");
        JButton expenseAnalyzerButton = new JButton("Expense Analyzer");
        JButton expenseSplitterButton = new JButton("Expense Splitter");
        JButton addExpenseOweButton = new JButton("Add Expense You Owe");
        JButton payFriendButton = new JButton("Pay Friend");
        JButton exitButton = new JButton("Exit");

        addPersonalExpenseButton.addActionListener(e -> showAddPersonalExpensePanel());
        expenseAnalyzerButton.addActionListener(e -> showExpenseAnalyzerPanel());
        expenseSplitterButton.addActionListener(e -> showExpenseSplitterPanel());
        addExpenseOweButton.addActionListener(e -> showAddExpenseYouOwePanel());
        payFriendButton.addActionListener(e -> showPayFriendPanel());
        exitButton.addActionListener(e -> System.exit(0));
        
        menuPanel.add(addPersonalExpenseButton);
        menuPanel.add(expenseAnalyzerButton);
        menuPanel.add(expenseSplitterButton);
        menuPanel.add(addExpenseOweButton);
        menuPanel.add(payFriendButton);
        menuPanel.add(exitButton);
        
        mainPanel.add(menuPanel, "Menu");

        // Add different panels to mainPanel
        mainPanel.add(createAddPersonalExpensePanel(), "AddPersonalExpense");
        mainPanel.add(createExpenseAnalyzerPanel(), "ExpenseAnalyzer");
        mainPanel.add(createExpenseSplitterPanel(), "ExpenseSplitter");
        mainPanel.add(createAddExpenseYouOwePanel(), "AddExpenseYouOwe");
        mainPanel.add(createPayFriendPanel(), "PayFriend");

        add(mainPanel);
    }

    private void showAddPersonalExpensePanel() {
        cardLayout.show(mainPanel, "AddPersonalExpense");
    }

    private void showExpenseAnalyzerPanel() {
        cardLayout.show(mainPanel, "ExpenseAnalyzer");
    }

    private void showExpenseSplitterPanel() {
        cardLayout.show(mainPanel, "ExpenseSplitter");
    }

    private void showAddExpenseYouOwePanel() {
        cardLayout.show(mainPanel, "AddExpenseYouOwe");
    }

    private void showPayFriendPanel() {
        cardLayout.show(mainPanel, "PayFriend");
    }

    private JPanel createAddPersonalExpensePanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        
        JLabel nameLabel = new JLabel("Enter your name:");
        JTextField nameField = new JTextField();
        JLabel amountLabel = new JLabel("Enter expense amount:");
        JTextField amountField = new JTextField();
        JLabel categoryLabel = new JLabel("Enter expense category:");
        JTextField categoryField = new JTextField();
        JLabel dateLabel = new JLabel("Enter expense date (yyyy-MM-dd):");
        JTextField dateField = new JTextField();
        
        JButton addButton = new JButton("Add Expense");
        JButton backButton = new JButton("Back");

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            LocalDate date = LocalDate.parse(dateField.getText());

            Finance_Analyser.Expense expense = new Finance_Analyser.Expense(name, amount, category, date);
            try {
                Finance_Analyser.DatabaseManager.insertExpense(expense);
                app.addExpense(expense);
                JOptionPane.showMessageDialog(this, "Expense added successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding expense: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(categoryLabel);
        panel.add(categoryField);
        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(addButton);
        panel.add(backButton);

        return panel;
    }

    private JPanel createExpenseAnalyzerPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        
        JLabel searchLabel = new JLabel("Search expenses by:");
        JComboBox<String> searchTypeComboBox = new JComboBox<>(new String[]{"Category", "User"});
        JTextField searchField = new JTextField();
        
        JTextArea resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        
        JButton searchButton = new JButton("Search");
        JButton backButton = new JButton("Back");

        searchButton.addActionListener(e -> {
            String searchType = (String) searchTypeComboBox.getSelectedItem();
            String searchText = searchField.getText();
            List<Finance_Analyser.Expense> expenses;

            if ("Category".equals(searchType)) {
                expenses = app.searchExpensesByCategory(searchText);
            } else {
                expenses = app.searchExpensesByUser(searchText);
            }

            resultArea.setText("");
            if (expenses.isEmpty()) {
                resultArea.setText("No expenses found.");
            } else {
                expenses.forEach(expense -> resultArea.append(expense.toString() + "\n"));
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));

        panel.add(searchLabel);
        panel.add(searchTypeComboBox);
        panel.add(new JLabel("Enter search text:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(backButton);
        panel.add(new JScrollPane(resultArea));

        return panel;
    }

    private JPanel createExpenseSplitterPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2));

        JLabel nameLabel = new JLabel("Enter your name:");
        JTextField nameField = new JTextField();
        JLabel friendsLabel = new JLabel("Enter your friends' names:");
        JTextField friendsField = new JTextField();
        JLabel amountLabel = new JLabel("Enter expense amount:");
        JTextField amountField = new JTextField();
        
        JLabel splitTypeLabel = new JLabel("How would you like to split the expense?");
        JComboBox<String> splitTypeComboBox = new JComboBox<>(new String[]{"Equally", "In percentages"});
        JTextField percentagesField = new JTextField();
        
        JButton splitButton = new JButton("Split Expense");
        JButton backButton = new JButton("Back");

        splitButton.addActionListener(e -> {
            String userName = nameField.getText();
            List<String> friendsList = Arrays.asList(friendsField.getText().split(","));
            double amount = Double.parseDouble(amountField.getText());
            String splitType = (String) splitTypeComboBox.getSelectedItem();
            
            Map<String, Double> splitAmounts;

            if ("Equally".equals(splitType)) {
                splitAmounts = app.splitExpenseEqually(amount, userName, friendsList);
            } else {
                String percentages = percentagesField.getText();
                splitAmounts = app.splitExpenseInPercentages(amount, userName, friendsList, percentages);
            }

            JOptionPane.showMessageDialog(this, splitAmounts.toString());
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(friendsLabel);
        panel.add(friendsField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(splitTypeLabel);
        panel.add(splitTypeComboBox);
        panel.add(new JLabel("Enter percentages (if applicable):"));
        panel.add(percentagesField);
        panel.add(splitButton);
        panel.add(backButton);

        return panel;
    }

    private JPanel createAddExpenseYouOwePanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel userNameLabel = new JLabel("Enter your name:");
        JTextField userNameField = new JTextField();
        JLabel friendNameLabel = new JLabel("Enter friend's name:");
        JTextField friendNameField = new JTextField();
        JLabel amountLabel = new JLabel("Enter amount you owe:");
        JTextField amountField = new JTextField();
        JLabel categoryLabel = new JLabel("Enter expense category:");
        JTextField categoryField = new JTextField();

        JButton addButton = new JButton("Add Expense");
        JButton backButton = new JButton("Back");

        addButton.addActionListener(e -> {
            String userName = userNameField.getText();
            String friendName = friendNameField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();

            try {
                Finance_Analyser.DatabaseManager.insertExpenseOwed(userName, friendName, amount, category);
                JOptionPane.showMessageDialog(this, "Expense added successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding expense: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));

        panel.add(userNameLabel);
        panel.add(userNameField);
        panel.add(friendNameLabel);
        panel.add(friendNameField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(categoryLabel);
        panel.add(categoryField);
        panel.add(addButton);
        panel.add(backButton);

        return panel;
    }

    private JPanel createPayFriendPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));

        JLabel userNameLabel = new JLabel("Enter your name:");
        JTextField userNameField = new JTextField();
        JTextArea resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        
        JLabel friendNameLabel = new JLabel("Enter friend's name to pay:");
        JTextField friendNameField = new JTextField();
        JLabel amountLabel = new JLabel("Enter the amount you want to pay:");
        JTextField amountField = new JTextField();

        JButton payButton = new JButton("Pay");
        JButton backButton = new JButton("Back");

        payButton.addActionListener(e -> {
            String userName = userNameField.getText();
            String friendName = friendNameField.getText();
            double paymentAmount = Double.parseDouble(amountField.getText());

            try {
                List<Finance_Analyser.Expense> expensesOwed = Finance_Analyser.DatabaseManager.getExpensesOwed(userName);
                resultArea.setText("");
                if (expensesOwed.isEmpty()) {
                    resultArea.setText("No expenses found.");
                    return;
                }

                for (Finance_Analyser.Expense expense : expensesOwed) {
                    resultArea.append(expense.toString() + "\n");
                }

                Finance_Analyser.DatabaseManager.payFriend(userName, friendName, paymentAmount);
                JOptionPane.showMessageDialog(this, "Payment successful!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error fetching or paying expenses: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));

        panel.add(userNameLabel);
        panel.add(userNameField);
        panel.add(new JScrollPane(resultArea));
        panel.add(friendNameLabel);
        panel.add(friendNameField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(payButton);
        panel.add(backButton);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinanceAnalyserGUI gui = new FinanceAnalyserGUI();
            gui.setVisible(true);
        });
    }
}

class Finance_Analyser {
    // Trie data structure to store expenses by category
    private final Trie trie = new Trie();

    // Graph data structure to store friendships
    private final Graph graph = new Graph();

    // Priority queue to store expenses in descending order
    private final PriorityQueue<Expense> expenseQueue = new PriorityQueue<>(Comparator.comparingDouble(e -> e.amount));

    void addExpense(Expense expense) {
        trie.insert(expense.category, expense.name, expense);
        expenseQueue.add(expense);
    }

    List<Expense> searchExpensesByCategory(String category) {
        return trie.searchByCategory(category);
    }

    List<Expense> searchExpensesByUser(String user) {
        return trie.searchByUser(user);
    }

    Map<String, Double> splitExpenseEqually(double amount, String userName, List<String> friends) {
        graph.addUser(userName);
        for (String friend : friends) {
            graph.addUser(friend);
            graph.addEdge(userName, friend);
        }
        return graph.splitExpenseEqually(amount, userName, friends);
    }

    Map<String, Double> splitExpenseInPercentages(double amount, String userName, List<String> friends, String percentages) {
        graph.addUser(userName);
        for (String friend : friends) {
            graph.addUser(friend);
            graph.addEdge(userName, friend);
        }
        return graph.splitExpenseInPercentages(amount, userName, friends, percentages);
    }

    private static class Trie {
        private Node root = new Node();

        public void insert(String category, String name, Expense expense) {
            Node current = root;
            for (char c : category.toCharArray()) {
                current = current.children.computeIfAbsent(c, k -> new Node());
            }
            current.expenses.add(expense);
            current.users.computeIfAbsent(name, k -> new ArrayList<>()).add(expense);
        }

        public List<Expense> searchByCategory(String category) {
            Node current = root;
            for (char c : category.toCharArray()) {
                current = current.children.get(c);
                if (current == null) {
                    return new ArrayList<>();
                }
            }
            return current.expenses;
        }

        public List<Expense> searchByUser(String user) {
            List<Expense> userExpenses = new ArrayList<>();
            searchByUser(root, user, userExpenses);
            return userExpenses;
        }

        private void searchByUser(Node node, String user, List<Expense> userExpenses) {
            if (node.users.containsKey(user)) {
                userExpenses.addAll(node.users.get(user));
            }
            for (Node child : node.children.values()) {
                searchByUser(child, user, userExpenses);
            }
        }

        private static class Node {
            Map<Character, Node> children = new HashMap<>();
            Map<String, List<Expense>> users = new HashMap<>();
            List<Expense> expenses = new ArrayList<>();
        }
    }

    private static class Graph {
        private Map<String, Node> nodes = new HashMap<>();

        public void addUser(String name) {
            nodes.computeIfAbsent(name, k -> new Node(name));
        }

        public void addEdge(String from, String to) {
            Node fromNode = nodes.get(from);
            Node toNode = nodes.get(to);
            fromNode.friends.add(toNode);
            toNode.friends.add(fromNode);
        }

        public Map<String, Double> splitExpenseEqually(double amount, String userName, List<String> friends) {
            Map<String, Double> splitAmounts = new HashMap<>();
            double totalFriends = friends.size();
            double amountPerFriend = amount / (totalFriends + 1); // Including the user who is paying
            for (String friend : friends) {
                splitAmounts.put(friend, amountPerFriend);
            }
            return splitAmounts;
        }

        public Map<String, Double> splitExpenseInPercentages(double amount, String userName, List<String> friends, String percentages) {
            Map<String, Double> splitAmounts = new HashMap<>();
            String[] percentageArray = percentages.split(",");
            for (int i = 0; i < friends.size(); i++) {
                String friend = friends.get(i);
                double percentage = Double.parseDouble(percentageArray[i].trim()) / 100;
                double splitAmount = amount * percentage;
                splitAmounts.put(friend, splitAmount);
            }
            return splitAmounts;
        }

        private static class Node {
            String name;
            List<Node> friends = new ArrayList<>();

            public Node(String name) {
                this.name = name;
            }
        }
    }

    public static class ExpenseComparator implements Comparator<Expense> {
        @Override
        public int compare(Expense e1, Expense e2) {
            return Double.compare(e2.amount, e1.amount);
        }
    }

    public static class Expense implements Comparable<Expense> {
        String name;
        double amount;
        String category;
        LocalDate date;

        public Expense(String name, double amount, String category, LocalDate date) {
            this.name = name;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }

        @Override
        public int compareTo(Expense e) {
            return Double.compare(e.amount, this.amount);
        }

        @Override
        public String toString() {
            return "Name: " + name + ", Amount: " + amount + ", Category: " + category + ", Date: " + date;
        }
    }
    class DatabaseManager {
        private static final String DB_URL = "jdbc:mysql://localhost:3306/splitwise";
        private static final String DB_USER = "root"; // Replace with your MySQL username
        private static final String DB_PASSWORD = "Innovation5376$"; // Replace with your MySQL password
    
        public static void insertExpense(Finance_Analyser.Expense expense) throws SQLException {
            String sql = "INSERT INTO expenses (name, amount, category, date) VALUES (?, ?, ?, ?)";
    
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
    
                statement.setString(1, expense.name);
                statement.setDouble(2, expense.amount);
                statement.setString(3, expense.category);
                statement.setDate(4, java.sql.Date.valueOf(expense.date));
    
                statement.executeUpdate();
            }
        }
        public static void insertExpenseOwed(String userName, String friendName, double amount, String category) throws SQLException {
            String sql = "INSERT INTO transactions (user_name, friend_name, amount, category) VALUES (?, ?, ?, ?)";
    
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
    
                statement.setString(1, userName);
                statement.setString(2, friendName);
                statement.setDouble(3, amount);
                statement.setString(4, category);
    
                statement.executeUpdate();
            }
        }
    
        public static List<Finance_Analyser.Expense> getExpensesOwed(String userName) throws SQLException {
            List<Finance_Analyser.Expense> expenses = new ArrayList<>();
            String sql = "SELECT * FROM transactions WHERE user_name = ?";
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String friendName = resultSet.getString("friend_name");
                        double amount = resultSet.getDouble("amount");
                        String category = resultSet.getString("category");
                        expenses.add(new Finance_Analyser.Expense(friendName, amount, category, LocalDate.now())); // Assuming current date for simplicity
                    }
                }
            }
            return expenses;
        }
    
        public static void payFriend(String userName, String friendName, double paymentAmount) throws SQLException {
            String sql = "DELETE FROM transactions WHERE user_name = ? AND friend_name = ? AND amount = ? LIMIT 1";
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userName);
                statement.setString(2, friendName);
                statement.setDouble(3, paymentAmount);
                statement.executeUpdate();
            }
        }
        
    }
    
}
