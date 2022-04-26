<?php
    if (isset($_POST["user_id"])){
        $user = $_POST["user_id"];
        $friend = $_POST["friend_id"];
        $status = 0;

        if (isset($_POST["status"])){
            $status = $_POST["status"];
        }

        $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");

        if ($mysqli->connect_error){
            die("Connection failed: " . $mysqli->connect_error);
        }

        $stmt = $mysqli->prepare("INSERT INTO friends(user_id, friend_id, status) VALUES (?, ?, ?)");
        $stmt->bind_param("iii", $user, $friend, $status);

        $stmt->execute();

        echo "200";
    }

    else {
        echo "400";
    }
?>