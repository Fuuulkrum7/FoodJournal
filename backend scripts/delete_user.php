<?php
    if (isset($_POST["user_id"])){
        $user_id = intval($_POST["user_id"]);

        $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");

        if ($mysqli->connect_error){
            die("Connection failed: " . $mysqli->connect_error);
        }

        $stmt = $mysqli->prepare("DELETE FROM users WHERE user_id=?");
        $stmt->bind_param("i", $id, $local_id);

        $stmt->execute();

        echo "200";
    }
    else {
        echo "400";
    }
?>