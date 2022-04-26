<?php
    if (isset($_POST["user_id"])){

        $user = $_POST["user_id"];
        $friend = $_POST["friend_id"];
        $status = intval($_POST["status"]);

        $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");

        if ($mysqli->connect_error){
            die("Connection failed: " . $mysqli->connect_error);
        }

        if ($status != -1){
            $stmt = $mysqli->prepare("UPDATE friends set status=? VALUES WHERE user_id=? AND friend_id=?");
            $stmt->bind_param("iii", $status, $user, $friend);
        }
        else {
            $stmt = $mysqli->prepare("DELETE FROM friends WHERE (user_id=? AND friend_id=?) OR (user_id=? AND friend_id=?)");
            $stmt->bind_param("iiii", $user, $friend, $friend, $user);
        }

        $stmt->execute();

        echo "200";
    }

    else {
        echo "400";
    }
?>