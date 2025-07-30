package NEW_BTLOOP;

public class Library {
    public static Document[] documents;// Mảng chứa các tài liệu
    protected  User[] users;// Mảng chứa các người dùng
    protected  int documentCount; // Số lượng tài liệu hiện có
    protected  int userCount; // Số lượng người dùng hiện có

    // Constructor
    public Library(int maxDocuments, int maxUsers) {
        documents = new Document[maxDocuments];
        users = new User[maxUsers];
        documentCount = 0;
        userCount = 0;
    }

    // Thêm tài liệu
    public void addDocument(Document doc) {
        if (documentCount < documents.length) {
            documents[documentCount++] = doc;
            System.out.println("Tài liệu đã được thêm thành công.");
        } else {
            System.out.println("Không thể thêm tài liệu. Đã đạt giới hạn tối đa.");
        }
    }

    // Thêm người dùng
    public void addUser(User user) {
        if (userCount < users.length) {
            users[userCount++] = user;
            System.out.println("Người dùng đã được thêm thành công.");
        } else {
            System.out.println("Không thể thêm người dùng. Đã đạt giới hạn tối đa.");
        }
    }
}