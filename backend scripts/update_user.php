<?php
    if (isset($_POST["password"])){
        $user_id = intval($_POST["user_id"]);
        $password = $_POST["password"];
        $username = $_POST["username"];

        $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");

        if ($mysqli->connect_error){
            die("Connection failed: " . $mysqli->connect_error);
        }

        $stmt = $mysqli->prepare("UPDATE users set password=?, username=? VALUES WHERE user_id=?");
        $stmt->bind_param("ssi", $username, $password, $user_id);

        $stmt->execute();

        echo "200";
    }
    else {
        echo "400";
    }
?>