package packagechung;

public class User {
    protected String name;
    protected String email;

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    public void searchBook(String keyword){
        System.out.println(name + "đang tìm kiếm sách với từ khóa: " + keyword);
    }
}
