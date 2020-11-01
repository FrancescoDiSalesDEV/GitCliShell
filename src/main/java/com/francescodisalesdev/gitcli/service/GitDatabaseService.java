package com.francescodisalesdev.gitcli.service;

import com.francescodisalesdev.gitcli.design.pattern.singleton.databaseSingleton;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GitDatabaseService
{

    public void createDatabase(String fileName,String path) throws SQLException
    {
        String url = "jdbc:sqlite:"+path+fileName+".db";

        databaseSingleton singleton = databaseSingleton.getSingletonInstance(url);

        String queryCreation = "create table author( author_id  INTEGER PRIMARY KEY AUTOINCREMENT, username text);";
        String queryRepositoryCreation = "create table repository( author_id INTEGER,repository_id text,name text, primary key(repository_id),foreign key(author_id) references author(id));";

        singleton.createTableStatement(queryCreation);
        singleton.createTableStatement(queryRepositoryCreation);

    }

    public void insertAuthor(String author,String path) throws SQLException
    {
        String url = "jdbc:sqlite:"+path;
        databaseSingleton singleton = databaseSingleton.getSingletonInstance(url);

        String queryInsert = "insert into author(username) values (?);";

        PreparedStatement insertAuthorStatement = singleton.getConnection().prepareStatement(queryInsert);
        insertAuthorStatement.setString(1,author);
        insertAuthorStatement.execute();

    }

    public void assignRepository(String author,String repository,String urlRepo,String path) throws SQLException
    {
        String url = "jdbc:sqlite:"+path;

        databaseSingleton singleton = databaseSingleton.getSingletonInstance(url);

        String selectQuery = "Select id from author where username=?";
        PreparedStatement selectAuthorStatement = singleton.getConnection().prepareStatement(selectQuery);
        selectAuthorStatement.setString(1,author);
        ResultSet authorFind = selectAuthorStatement.executeQuery();

        while(authorFind.next())
        {
            String queryInsert = "insert into repository(author_id,name,url) values(?,?,?);";
            PreparedStatement assignRepositoryStatemtent = singleton.getConnection().prepareStatement(queryInsert);

            assignRepositoryStatemtent.setString(1,authorFind.getString(1));
            assignRepositoryStatemtent.setString(2,repository);
            assignRepositoryStatemtent.setString(3,urlRepo);

            assignRepositoryStatemtent.execute();
        }

    }

    public void searchAuthor(String author,String path) throws SQLException
    {
        String url = "jdbc:sqlite:"+path;

        databaseSingleton singleton = databaseSingleton.getSingletonInstance(url);

        String selectQuery;

        if(author.equals("*"))
         selectQuery = "Select * from author;";
        else
         selectQuery = "Select username from author where username=?";

        PreparedStatement selectAuthorStatement = singleton.getConnection().prepareStatement(selectQuery);

        if(!author.equals("*"))
            selectAuthorStatement.setString(1,author);

        ResultSet resultSetAuthor = selectAuthorStatement.executeQuery();

        while(resultSetAuthor.next())
        {
            if(author.equals("*"))
              System.out.println(resultSetAuthor.getString(2));
            else
              System.out.println(resultSetAuthor.getString(1));
        }
    }

    public void searchRepositoryByAuthor(String author,String path) throws SQLException
    {
        String url = "jdbc:sqlite:"+path;
        databaseSingleton singleton = databaseSingleton.getSingletonInstance(url);

        String queryJoinRepository = "Select username from author a,repository r where a.author_id=r.author_id and a.username=? ";
        PreparedStatement repositoryAuthorStatement = singleton.getConnection().prepareStatement(queryJoinRepository);
        repositoryAuthorStatement.setString(1,author);

        ResultSet resultSetJOIN = repositoryAuthorStatement.executeQuery();

        while(resultSetJOIN.next())
        {
            System.out.println(resultSetJOIN.getString(1));
        }

    }


}