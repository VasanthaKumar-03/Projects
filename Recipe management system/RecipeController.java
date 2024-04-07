package com.example.Recipe.management.system.Controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.rmi.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class RecipeController {
    String username;
    @GetMapping("/start")
    public String start() {
        this.username=null;
        return "login";
    }

    @GetMapping("/create")
    public String Create(){
        return "create_new_acc";
    }

    @GetMapping("/hello")
    public String hello(){
        this.username=null;
        System.out.println("Inside hello method");
        return "login";
    }
    @GetMapping("/guestAlert")
    public String guestAlert(){
        this.username=null;
        return "guestAlert";
    }

    @GetMapping("/addrecipe_page")
    public String addrecipe_page(){
        return "addRecipe";
    }
    @PostMapping("/submit")
    public String dashboard(@RequestParam("username") String username, @RequestParam("password") String password){
        String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
        Connection connection = null;
        this.username=username;
        System.out.println("inside submit ");
        try{
            connection = DriverManager.getConnection(jdbcurl, "root", "admin");
            String sql = "select password from User where username=?";
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setString(1,username);
            ResultSet rs=pstatement.executeQuery();
            while(rs.next()){
                if(password.equals(rs.getString("password"))){
                    System.out.println("successfully logged in");
                    return "index";
                }
                else {
                    System.out.println("Wrong credentials");
                    return "alert";
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "create_new_acc";
    }

    @PostMapping("/createAcc")
    public String createAcc(@RequestParam String Emailid, @RequestParam String Userid, @RequestParam String Username, @RequestParam String Password){
        String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(jdbcurl, "root", "admin");
            String sql = "insert into User values(?,?,?,?)";
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setString(1,Userid);
            pstatement.setString(2,Username);
            pstatement.setString(3,Password);
            pstatement.setString(4,Emailid);
            pstatement.execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "login";
    }
    @PostMapping("/addrecipe")
    public String addRecipe(@RequestParam("recipename") String recipename,@RequestParam("ingredients") String ingredients,@RequestParam("instructions") String instructions,@RequestParam("cookingtime") String cookingtime,@RequestParam("category") String category){
        String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(jdbcurl, "root", "admin");
            String sql = "insert into Recipe(username, Recipe_name, ingredients, Instructions, cooking_time, category) values(?,?,?,?,?,?)";
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setString(1,this.username);
            pstatement.setString(2,recipename);
            pstatement.setString(3,ingredients);
            pstatement.setString(4,instructions);
            pstatement.setString(5,cookingtime);
            pstatement.setString(6,category);
            pstatement.execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "addAlert";
    }

    @GetMapping("/viewRecipes")
    public String viewRecipes(Model model){
        List<Map<String,Object>> data = fetchRecipe();
//        model.addAttribute("recipeList",data);
        return "index";
    }
    @ModelAttribute("recipeList")
    public List<Map<String,Object>> fetchRecipe(){
        List<Map<String,Object>> listoffrecipes = new ArrayList<>();
        System.out.println(("The username is "+username));
        String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
        try (Connection connection = DriverManager.getConnection(jdbcurl, "root", "admin")) {
            String sql = "select * from Recipe";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Map<String,Object> mp = new HashMap<>();
                mp.put("Recipename", rs.getString("Recipe_name"));
                mp.put("Cookingtime", rs.getString("cooking_time"));
                mp.put("Category", rs.getString("category"));
                listoffrecipes.add(mp);
            }
            System.out.println("The map data are "+listoffrecipes);
        } catch (SQLException e) {
            System.out.println("The exception occurred: " + e);
        }
        return listoffrecipes;
    }

    @GetMapping("/viewInstructions")
    public String viewInstructions(@RequestParam("selectedRow") String selectedRow){
        System.out.println("inside view");
        List<Map<String,Object>> data = fetchInstructions(selectedRow);
//     model.addAttribute("recipeList",data);
        return "instruction";
    }
    @ModelAttribute("instructionList")
    public List<Map<String,Object>> fetchInstructions(String selectedRow){
        System.out.println("The selected recipe is "+selectedRow);
        List<Map<String,Object>> listofinstructions = new ArrayList<>();

        String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
        Connection connection = null;
        try{

            connection = DriverManager.getConnection(jdbcurl, "root", "admin");
            String sql = "select * from Recipe where Recipe_name=?";
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setString(1,selectedRow);
            ResultSet rs = pstatement.executeQuery();
            while(rs.next())
            {
            Map<String,Object> mp = new HashMap<>();
            mp.put("Recipename", rs.getString("Recipe_name"));
            mp.put("Cookingtime", rs.getString("cooking_time"));
            mp.put("Category", rs.getString("category"));
            mp.put("Ingredients", rs.getString("ingredients"));
            mp.put("Instructions", rs.getString("Instructions"));
            listofinstructions.add(mp);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return listofinstructions;
    }

    @GetMapping("/profile")
    public String profile(){
        System.out.println("inside profile");
        List<Map<String,Object>> data = fetchProfile();
//      model.addAttribute("recipeList",data);
        return "profile";
    }
    @ModelAttribute("profileList")
    public List<Map<String,Object>> fetchProfile(){
        //System.out.println("The selected recipe is "+selectedRow);
        List<Map<String,Object>> listofinstructions = new ArrayList<>();
        System.out.println(this.username);
        String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
        Connection connection = null;
        try{

            connection = DriverManager.getConnection(jdbcurl, "root", "admin");
            String sql = "select * from Recipe where username=?";
            //String uname=this.username;
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setString(1,this.username);
            ResultSet rs = pstatement.executeQuery();
            while(rs.next())
            {
                Map<String,Object> mp = new HashMap<>();
                mp.put("Recipename", rs.getString("Recipe_name"));
                mp.put("Cookingtime", rs.getString("cooking_time"));
                mp.put("Category", rs.getString("category"));
                mp.put("Ingredients", rs.getString("ingredients"));
                mp.put("Instructions", rs.getString("Instructions"));
                listofinstructions.add(mp);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return listofinstructions;
    }
    @GetMapping("/editRecipe")
    public String editRecipe(@RequestParam("selectedRow") String selectedRow){
        if(selectedRow.contains("delete")){
            String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
//            System.out.println(selectedRow);
            Connection connection = null;
            try{
                connection = DriverManager.getConnection(jdbcurl, "root", "admin");
                String sql = "DELETE FROM Recipe WHERE Recipe_name=?";
                selectedRow =selectedRow.replaceFirst("delete","");
                PreparedStatement pstatement = connection.prepareStatement(sql);
                System.out.println(selectedRow);
                pstatement.setString(1,selectedRow);
                pstatement.execute();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return "delete";
        }else{
            System.out.println("inside edit");
            List<Map<String,Object>> data = fetchdata(selectedRow);
//          model.addAttribute("recipeList",data);
            return "edit";
        }
    }
    @ModelAttribute("editList")
    public List<Map<String,Object>> fetchdata(String selectedRow){
        System.out.println("The selected recipe is "+selectedRow);
        List<Map<String,Object>> listofinstructions = new ArrayList<>();

        String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(jdbcurl, "root", "admin");
            String sql = "select * from Recipe where Recipe_name=?";
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setString(1,selectedRow);
            ResultSet rs = pstatement.executeQuery();
            while(rs.next())
            {
                Map<String,Object> mp = new HashMap<>();
                mp.put("Recipename", rs.getString("Recipe_name"));
                mp.put("Cookingtime", rs.getString("cooking_time"));
                mp.put("Category", rs.getString("category"));
                mp.put("Ingredients", rs.getString("ingredients"));
                mp.put("Instructions", rs.getString("Instructions"));
                listofinstructions.add(mp);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return listofinstructions;
    }
    @PostMapping("/edited")
    public String edited(@RequestParam("recipename") String recipename,@RequestParam("ingredients") String ingredients,@RequestParam("instructions") String instructions,@RequestParam("cookingtime") String cookingtime,@RequestParam("category") String category){
        String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(jdbcurl, "root", "admin");
            String sql = "UPDATE Recipe SET Recipe_name =?, ingredients =?,Instructions =?,cooking_time =?,category =? WHERE Recipe_name=?";
            PreparedStatement pstatement = connection.prepareStatement(sql);

            pstatement.setString(1,recipename);
            pstatement.setString(2,ingredients);
            pstatement.setString(3,instructions);
            pstatement.setString(4,cookingtime);
            pstatement.setString(5,category);
            pstatement.setString(6,recipename);
            pstatement.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "editAlert";
    }

    @GetMapping("/viewGuestRecipes")
    public String viewGuestRecipes(Model model){
        List<Map<String,Object>> data = fetchGuestRecipe();
//        model.addAttribute("recipeList",data);
        return "guestIndex";
    }
    @ModelAttribute("recipeList")
    public List<Map<String,Object>> fetchGuestRecipe(){
        List<Map<String,Object>> listoffrecipes = new ArrayList<>();
        System.out.println(("The username is "+username));
        String jdbcurl = "jdbc:mysql://127.0.0.1:3306/recipe_db";
        try (Connection connection = DriverManager.getConnection(jdbcurl, "root", "admin")) {
            String sql = "select * from Recipe";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Map<String,Object> mp = new HashMap<>();
                mp.put("Recipename", rs.getString("Recipe_name"));
                mp.put("Cookingtime", rs.getString("cooking_time"));
                mp.put("Category", rs.getString("category"));
                listoffrecipes.add(mp);
            }
            System.out.println("The map data are "+listoffrecipes);
        } catch (SQLException e) {
            System.out.println("The exception occurred: " + e);
        }
        return listoffrecipes;
    }

}
