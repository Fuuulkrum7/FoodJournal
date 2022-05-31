<?php
    require_once("check.php");

    if (isset($_POST["login"])){
        $result = checkUser($_POST["login"], $_POST["password"]);

        echo $result;
    }
?>