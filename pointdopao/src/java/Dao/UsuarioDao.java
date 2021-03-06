/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dao;

import Controllers.CadastrarController;
import Models.Usuario;

import javax.servlet.RequestDispatcher;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bcovies
 */
public class UsuarioDao {

    private String jdbcURL = "jdbc:postgresql://localhost:12002/pointdopao";

    private String jdbcUsername = "admin";
    private String jdbcPassword = "admin";

    private static final String INSERT_USERS_SQL = "INSERT INTO usuario (tipo, nome, sobrenome, email, senha) VALUES (?,?,?,?,?);";

    private static final String SELECT_USER_BY_EMAIL_PASS = "SELECT * FROM usuario WHERE email = ? AND senha = ?;";
    private static final String SELECT_USER_BY_EMAIL = "SELECT email FROM usuario WHERE email = ?";
    private static final String SELECT_NAME_BY_EMAIL = "SELECT nome FROM usuario WHERE email = ?";
    private static final String UPDATE_USER_PASS_BY_EMAIL = "UPDATE usuario SET senha = ? WHERE email = ?";
    private static final String UPDATE_USER_ENDERECO_BY_ID =
            "UPDATE usuario " +
                    "SET logradouro=?, numero=?, complemento=?, bairro=?, estado=?, cep=? " +
                    "WHERE id = ?";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM usuario WHERE id = ?";

//    private static final String SELECT_ALL_USERS = "select * from usuario";
//    private static final String DELETE_USERS_SQL = "delete from usuario where id = ?;";
//    private static final String UPDATE_USERS_SQL = "update usuario set nome = ?,sobrenome= ?, email =? where id = ?;";
    protected Connection getConnection() {

        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //System.out.println("\nUSUARIODAO:\nIniciada a conexão com o banco de dados:\t" + connection);
        return connection;
    }

    public void insertUser(Usuario user) throws SQLException {

        // try-with-resource statement will auto close the connection.
        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setInt(1, user.getTipo());
            preparedStatement.setString(2, user.getNome());
            preparedStatement.setString(3, user.getSobrenome());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getSenha());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            //System.out.println("\nUSUARIODAO:\n" + preparedStatement);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public Boolean authByEmailSenha(String email, String senha) {

        boolean autenticado = false;

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_EMAIL_PASS)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, senha);

            ResultSet rs;
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String loginBanco = rs.getString("email");
                String senhaBanco = rs.getString("senha");
                autenticado = true;
            }
            preparedStatement.executeUpdate();
            //System.out.println("\nUSUARIODAO:\n" + preparedStatement);
        } catch (Exception e) {
            e.getMessage();
        }
        return autenticado;
    }

    public boolean changeUserPass(String email, String senha) {
        boolean autenticado = false;
        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_PASS_BY_EMAIL)) {
            preparedStatement.setString(2, email.toUpperCase());
            preparedStatement.setString(1, senha);

            ResultSet rs;
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                preparedStatement.executeUpdate();
                autenticado = true;
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return autenticado;
    }

    public Usuario searchUsernameByEmail(String email, String senha) {
        Usuario usuario = new Usuario();

        try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_EMAIL_PASS)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, senha);

            ResultSet rs;
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
            }
            preparedStatement.executeUpdate();

            return usuario;
        } catch (Exception e) {
            //System.out.println("\n\nErro em searchUsernameByEmail : " + e.getMessage());
        }
        //System.out.println("\nNOME searchUsernameByEmail: " + nome);
        return usuario;
    }

    public boolean insertEndereco(String logradouro,
                                  String numero,
                                  String complemento,
                                  String bairro,
                                  String estado,
                                  String cep,int id) throws SQLException {

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_ENDERECO_BY_ID)) {
            preparedStatement.setString(1, logradouro);
            preparedStatement.setString(2, numero);
            preparedStatement.setString(3, complemento);
            preparedStatement.setString(4, bairro);
            preparedStatement.setString(5, estado);
            preparedStatement.setString(6, cep);
            preparedStatement.setInt(7, id);
            System.out.println(preparedStatement);
            ResultSet rs;
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                preparedStatement.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(CadastrarController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Usuario getEndereco(int id) {
        Usuario usuario = new Usuario();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);
            ResultSet rs;
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                if (rs.getString("logradouro") != null) {
                    usuario.setLogradouro(rs.getString("logradouro"));
                    usuario.setNumero(rs.getString("numero"));
                    usuario.setComplemento(rs.getString("complemento"));
                    usuario.setBairro(rs.getString("bairro"));
                    usuario.setEstado(rs.getString("estado"));
                    usuario.setCep(rs.getString("cep"));

                }
                preparedStatement.executeUpdate();
            }
            return usuario;
        } catch (SQLException ex) {
            Logger.getLogger(CadastrarController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return usuario;
    }

    public Usuario getUserById(int idUsuario) throws SQLException {
        Usuario usuario = new Usuario();
        // try-with-resource statement will auto close the connection.
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, idUsuario);
            System.out.println(preparedStatement);
            ResultSet rs;
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                usuario.setId(rs.getInt("id"));
                usuario.setTipo(rs.getInt("tipo"));
                usuario.setNome(rs.getString("nome"));
                usuario.setSobrenome(rs.getString("sobrenome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setLogradouro(rs.getString("logradouro"));
                usuario.setNumero(rs.getString("numero"));
                usuario.setComplemento(rs.getString("complemento"));
                usuario.setBairro(rs.getString("bairro"));
                usuario.setEstado(rs.getString("estado"));
                usuario.setCep(rs.getString("cep"));

                preparedStatement.executeUpdate();
            }

            preparedStatement.executeUpdate();
            //System.out.println("\nUSUARIODAO:\n" + preparedStatement);
        } catch (Exception e) {
            e.getMessage();
        }
        return usuario;
    }
}
