package com.example.birds_of_a_feather_team_20.model.db;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.birds_of_a_feather_team_20.Profile;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

@Entity(tableName = "courses")
public class Course {

    // Sessions
    public static final String FA = "FA";
    public static final String WI = "WI";
    public static final String SP = "SP";
    public static final String SS1 = "SS1";
    public static final String SS2 = "SS2";
    public static final String SSS = "SSS";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_id")
    private int courseId = 0;

    @ColumnInfo(name = "year")
    private int year;

    @ColumnInfo(name = "session")
    private String session;

    @ColumnInfo(name = "department")
    private String department;

    @ColumnInfo(name = "course_number")
    private String courseNumber; //Must be a string due to A/B courses

    @ColumnInfo(name = "course_classSize")
    private int classSize;

    @Ignore
    public Course(int year, String session, String department, String courseNumber){
        this.year = year;
        this.session = session;
        this.department = department;
        this.courseNumber = courseNumber;
    }
    public Course(int year, String session, String department, String courseNumber, int classSize){
        this.year = year;
        this.session = session;
        this.department = department;
        this.courseNumber = courseNumber;
        this.classSize = classSize;
    }

    public static final String[] courseSizeList = { "Tiny (<40)", "Small (40-75)",
            "Medium (75-150)", "Large (150-250)", "Huge (250-400)", "Gigantic (400+)" };


    public int getCourseId(){ return courseId; }

    public void setCourseId(int courseId){ this.courseId = courseId; }

    public int getYear() { return year; }

    public void setYear(int year) { this.year = year; }

    public String getSession() { return session; }

    public void setSession(String session) { this.session = session; }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    public String getCourseNumber() { return courseNumber; }

    public void setCourseNumber(String courseNumber) { this.courseNumber = courseNumber; }

    public int getClassSize() { return this.classSize; }

    public void setClassSize(int classSize) { this.classSize = classSize; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;


        return year == course.year
                && session.replaceAll(" ", "").equalsIgnoreCase(course.session.replaceAll(" ", ""))
                && department.replaceAll(" ", "").equalsIgnoreCase(course.department.replaceAll(" ", ""))
                && courseNumber.replaceAll(" ", "").equalsIgnoreCase(course.courseNumber.replaceAll(" ", ""));
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, year, session, department, courseNumber);
    }

//    public String serialize() {
//        StringWriter out = new StringWriter();
//        JsonWriter writer = new JsonWriter(out);
//        try {
//            writer.beginObject();
//            writer.name("course_year").value(this.getYear());
//            writer.name("course_session").value(this.getSession());
//            writer.name("course_department").value(this.getDepartment());
//            writer.name("course_number").value(this.getCourseNumber());
//            writer.name("course_size").value(this.getClassSize());
//            writer.endObject();
//            writer.close();
//            return out.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public static Course deserialize(String data) {
//        if (data == null) return null;
//        StringReader in = new StringReader(data);
//        JsonReader reader = new JsonReader(in);
//        int year = 0;
//        String session = "";
//        String department = "";
//        String number = "";
//        int size = 0;
//        Course course = null;
//        try {
//            // read name and URL
//            reader.beginObject();
//            while(reader.hasNext()) {
//                String key = reader.nextName();
//                if (reader.peek() == JsonToken.NULL) {
//                    reader.skipValue();
//                    continue;
//                }
//                switch (key) {
//                    case "course_year":
//                        year = reader.nextInt();
//                        break;
//                    case "course_session":
//                        session = reader.nextString();
//                        break;
//                    case "course_department":
//                        department = reader.nextString();
//                        break;
//                    case "course_number":
//                        number = reader.nextString();
//                        break;
//                    case "course_size":
//                        size = reader.nextInt();
//                        break;
//                    default:
//                        reader.skipValue();
//                        break;
//                }
//            }
//            reader.endObject();
//            reader.close();
//        } catch (IOException | IllegalStateException e) {
//            e.printStackTrace();
//        }
//        in.close();
//
//        course = new Course(year, session, department, number);
//        course.setClassSize(size);
//        return course;
//    }

    public void writeCourse(JsonWriter writer) throws IOException {
        //    private static void writeCourse(JsonWriter writer, Course course) throws IOException {
//        writer.beginObject();
//        writer.name("course_data").value(course.serialize());
//        writer.endObject();
//    }
        writer.beginObject();
        writer.name("course_year").value(this.getYear());
        writer.name("course_session").value(this.getSession());
        writer.name("course_department").value(this.getDepartment());
        writer.name("course_number").value(this.getCourseNumber());
        writer.name("course_size").value(this.getClassSize());
        writer.endObject();
    }

    public static Course readCourse(JsonReader reader) throws IOException {
        reader.beginObject();
        int year = 0;
        String session = "";
        String department = "";
        String number = "";
        int size = 0;
        Course course = null;
        while(reader.hasNext()) {
            String key = reader.nextName();
            if (reader.peek() == JsonToken.NULL) {
                reader.skipValue();
                continue;
            }
            switch (key) {
                case "course_year":
                    year = reader.nextInt();
                    break;
                case "course_session":
                    session = reader.nextString();
                    break;
                case "course_department":
                    department = reader.nextString();
                    break;
                case "course_number":
                    number = reader.nextString();
                    break;
                case "course_size":
                    size = reader.nextInt();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        Course c = new Course(year, session, department, number);
        c.setClassSize(size);
        return c;
    }

//    public String serialize() {
//        try {
//            StringWriter out = new StringWriter();
//            JsonWriter writer = new JsonWriter(out);
//            writeCourse(writer);
//            writer.close();
//            return out.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
}
