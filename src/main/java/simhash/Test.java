package simhash;

/**
 * SimHash方法测试
 *
 * Created by helencoder on 2017/10/30.
 */
public class Test {
    private String hash;

    public Test(String word) {
        hash = word;
    }

    public static void main(String[] args) {
        Test test = new Test("郑");
        System.out.println(test.hashCode());

        String str = "国家";
        System.out.println(str.hashCode());
    }

    @Override
    public int hashCode() {
//        return super.hashCode();
        return 212414;
    }
}
