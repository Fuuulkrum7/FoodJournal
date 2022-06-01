<?php
    require_once("check.php");

    if (isset($_POST["login"]) && isset($_POST["password"]) && isset($_POST["username"])){
        $login = $_POST["login"];
        $password = $_POST["password"];
        $username = $_POST["username"];

        $result = checkUser($login, $password);
        
        if ($result == "400"){
            
            $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");

            if ($mysqli->connect_error){
                die("Connection failed: " . $mysqli->connect_error);
            }

            $stmt = $mysqli->prepare("INSERT INTO users(login, password, username) VALUES (?, ?, ?)");
            $stmt->bind_param("sss", $login, $password, $username);

            $stmt->execute();

            $query = $mysqli->prepare("SELECT user_id FROM users WHERE login = (?)");
            $query->bind_param("s", $login);
            $query->execute();
            
            $res = $query->get_result();
            $row = $res->fetch_assoc();

            echo $row["user_id"];
        }
        else {
            echo "-2";
        }
    }
    else {
        echo "-1";
    }
?>