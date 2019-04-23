package com.example.generator.generator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

/**
 * <p>
 * TODO 根据数据库表生成javabean
 * </p>
 *
 * @author czs
 * @create 2019-04-23 11:37
 **/
public class GenEntityMysql {

    private String packageOutPath = "com.example.generator.base.entity";// 指定实体生成所在包的路径
    private String authorName = "Czs";// 作者名字
    //	private String tablename = "t_user";//表名
    private String[] colnames; // 列名数组
    private String[] colTypes; // 列名类型数组
    private int[] colSizes; // 列名大小数组
    private boolean f_util = false; // 是否需要导入包java.util.*
    private boolean f_sql = false; // 是否需要导入包java.sql.*

    // 数据库连接
    private static final String URL = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false";
    private static final String NAME = "root";
    private static final String PASS = "root";
    private static final String DRIVER = "com.mysql.jdbc.Driver";

    /*
     * 构造函数
     */
    public GenEntityMysql(String tablename) {
        // 创建连接
        Connection con;
        // 查要生成实体类的表
        String sql = "select * from " + tablename;
        PreparedStatement pStemt = null;
        try {
            try {
                Class.forName(DRIVER);
            } catch (ClassNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            con = DriverManager.getConnection(URL, NAME, PASS);
            pStemt = con.prepareStatement(sql);
            ResultSetMetaData rsmd = pStemt.getMetaData();
            int size = rsmd.getColumnCount(); // 统计列
            colnames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];
            for (int i = 0; i < size; i++) {
                colnames[i] = rsmd.getColumnName(i + 1);
                colTypes[i] = rsmd.getColumnTypeName(i + 1);

                if (colTypes[i].equalsIgnoreCase("datetime") || colTypes[i].equalsIgnoreCase("timestamp")) {
                    f_util = true;
                }
                if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
                    f_sql = true;
                }
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }

            String content = parse(colnames, colTypes, colSizes, tablename);

            try {
                File directory = new File("");
                // System.out.println("绝对路径："+directory.getAbsolutePath());
                // System.out.println("相对路径："+directory.getCanonicalPath());
                String path = this.getClass().getResource("").getPath();

                System.out.println(path);
                System.out.println("src/?/" + path.substring(path.lastIndexOf("/com/", path.length())));
//				String outputPath = directory.getAbsolutePath()+ "/src/"+path.substring(path.lastIndexOf("/com/", path.length()), path.length()) + initcap(tablename) + ".java";
                String outputPath = directory.getAbsolutePath() + "/src/main/java/"
                        + this.packageOutPath.replace(".", "/") + "/"
                        + initcap(convertUnderline(tablename.substring(tablename.indexOf("_") + 1))) + "Model"
                        + ".java";
                FileWriter fw = new FileWriter(outputPath);
                PrintWriter pw = new PrintWriter(fw);
                pw.println(content);
                pw.flush();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
//			try {
//				con.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
    }

    /**
     * 功能：生成实体类主体代码
     *
     * @param colnames
     * @param colTypes
     * @param colSizes
     * @return
     */
    private String parse(String[] colnames, String[] colTypes, int[] colSizes, String tablename) {
        StringBuffer sb = new StringBuffer();

        sb.append("package " + this.packageOutPath + ";\r\n");
        sb.append("\r\n");

        /*
         * 判断是否导入工具包
         */
        if (f_util) {
            sb.append("import java.util.Date;\r\n");
        }
        if (f_sql) {
            sb.append("import java.sql.*;\r\n");
        }

        /*
         * 导入包 import javax.persistence.Column; import javax.persistence.GeneratedValue;
         * import javax.persistence.GenerationType; import javax.persistence.Id;
         */
        sb.append("import javax.persistence.Column;\r\n");
        sb.append("import javax.persistence.GeneratedValue;\r\n");
        sb.append("import javax.persistence.GenerationType;\r\n");
        sb.append("import javax.persistence.Id;\r\n");
        sb.append("import javax.persistence.Entity;\r\n");
        sb.append("import javax.persistence.Table;\r\n");

        // 注释部分
        sb.append("   /**\r\n");
        sb.append("    * " + tablename + " 实体类\r\n");
        sb.append("    * " + authorName + "\r\n");
        sb.append("    * " + new Date() + "\r\n");
        sb.append("    */ \r\n");
        /*
         * 实体部分
         */
        sb.append("@Entity \r\n");
        sb.append("@Table(name = \"" + tablename + "\")");
        sb.append("\r\n\r\npublic class " + initcap(convertUnderline(tablename.substring(tablename.indexOf("_") + 1)))
                + "Model" + "{\r\n");
        processAllAttrs(sb);// 属性
        processAllMethod(sb);// get set方法
        sb.append("}\r\n");

        // System.out.println(sb.toString());
        return sb.toString();
    }

    /**
     * 功能：生成所有属性
     *
     * @param sb
     */
    private void processAllAttrs(StringBuffer sb) {

        for (int i = 0; i < colnames.length; i++) {
            if ("id".equals(colnames[i])) {
                sb.append("\t@Id" + "\r\n");
                sb.append("\t@GeneratedValue(strategy = GenerationType.IDENTITY)" + "\r\n");
            }

            sb.append("\t@Column(name =\"" + colnames[i] + "\")" + "\r\n");
            sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + convertUnderline(colnames[i]) + ";\r\n");
        }

    }

    /**
     * 功能：生成所有方法
     *
     * @param sb
     */
    private void processAllMethod(StringBuffer sb) {

        for (int i = 0; i < colnames.length; i++) {
            sb.append("\tpublic void set" + initcap(convertUnderline(colnames[i])) + "(" + sqlType2JavaType(colTypes[i])
                    + " " + convertUnderline(colnames[i]) + "){\r\n");
            sb.append("\tthis." + convertUnderline(colnames[i]) + "=" + convertUnderline(colnames[i]) + ";\r\n");
            sb.append("\t}\r\n");
            sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + initcap(convertUnderline(colnames[i]))
                    + "(){\r\n");
            sb.append("\t\treturn " + convertUnderline(colnames[i]) + ";\r\n");
            sb.append("\t}\r\n");
        }

    }

    /**
     * 功能：将输入字符串的首字母改成大写
     *
     * @param str
     * @return
     */
    private String initcap(String str) {

        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }

        return new String(ch);
    }

    /**
     * 功能：获得列的数据类型
     *
     * @param sqlType
     * @return
     */
    private String sqlType2JavaType(String sqlType) {

        if (sqlType.equalsIgnoreCase("bit")) {
            return "boolean";
        } else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "byte";
        } else if (sqlType.equalsIgnoreCase("smallint")) {
            return "short";
        } else if (sqlType.equalsIgnoreCase("int")) {
            return "int";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "float";
        } else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "double";
        } else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
                || sqlType.equalsIgnoreCase("text")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("timestamp")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("image")) {
            return "Blod";
        }

        return null;
    }

    /*
     * 讲含有_的名字 转成小驼峰 name_li => nameLi
     */
    public static String convertUnderline(String str) {
        char[] name = str.toCharArray();
        for (int i = 0; i < name.length; i++) {
            if ('_' == name[i]) {
                name[i + 1] -= 32;
            }
        }
        return String.valueOf(name).replaceAll("_", "");
    }

    /**
     * 出口 TODO
     *
     * @param args
     */
    public static void main(String[] args) {
        String[] tablename = { "user_info" };
        // String[] tablename = { "tb_article", "tb_article_category", "tb_article_tags", "tb_category", "tb_comments",
        //         "tb_links", "tb_tags", "tb_user" };

        for (int i = 0; i < tablename.length; i++) {
            new GenEntityMysql(tablename[i]);
        }

    }
}
