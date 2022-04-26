<?php
    if (isset($_POST["local_id"])){
        $local_id = intval($_POST["local_id"]);
        $user_id = intval($_POST["user_id"]);

        $dish = $_POST["dish"];
        $mass = intval($_POST["mass"]);
        $calories = intval($_POST["calories"]);
        $time = $_POST["time"];

        $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");

        if ($mysqli->connect_error){
            die("Connection failed: " . $mysqli->connect_error);
        }

        $stmt = $mysqli->prepare("UPDATE journal set dish=?, mass=?, calories=?, time=? VALUES WHERE user_id=? AND local_id=?");
        $stmt->bind_param("siisii", $dish, $mass, $calories, $time, $user_id, $local_id);

        $stmt->execute();

        echo "200";
    }
    else {
        echo "400";
    }
?>