<?php
    function checkUser($login, $password){
        $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");

        if ($mysqli->connect_error){
            die("Connection failed: " . $mysqli->connect_error);
        }

        $stmt = $mysqli->prepare("SELECT login, password FROM users WHERE login = (?)");
        $stmt->bind_param("s", $login);
        $stmt->execute();

        $result = $stmt->get_result();
        $row = $result->fetch_assoc();

        if ($row["login"] == $login and $row["password"] == $password){
            return "200";
        }
        elseif ($row["login"] == $login and $row["password"] != $password){
            return "300";
        }
        else{
            return "400";
        }
    }
?>