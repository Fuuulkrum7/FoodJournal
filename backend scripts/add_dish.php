<?php
    if (isset($_POST["dish"])){
        $local_id = intval($_POST["local_id"]);
        $dish = $_POST["dish"];
        $mass = intval($_POST["mass"]);
        $calories = intval($_POST["calories"]);
        $eating = intval($_POST["eating"]);

        $date = $_POST["date"];

        $time = $_POST["time"];
        $id = intval($_POST["user_id"]);
        
        $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");
        if ($mysqli->connect_error){
            die("Connection failed: " . $mysqli->connect_error);
        }
        
        $query = "INSERT INTO journal(local_id, dish, mass, calories, eating, user_id, date, time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        $stmt = $mysqli->prepare($query);

        $stmt->bind_param("isiiiiss", $local_id, $dish, $mass, $calories, $eating, $id, $date, $time);

        $stmt->execute();
        
        echo "200";
    }
    else {
        echo "400";
    }
?>