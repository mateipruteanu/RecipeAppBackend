# Restful API backend for a Recipe app.

* Built in **Java** with **Maven** and the **Spring Framework** using a MVC architecture.
* Using **Spring Data JPA** for persisting data into a **MySQL** database.

:green_circle: = Done
<br>
:yellow_circle: = In progress, but mostly done
<br>
:large_blue_circle: = Future plans
<br>


### What the app will do:
- 
- :green_circle: Users will be able to add their own recipes (with ingredients & instructions, later photos)
- :yellow_circle: They will be able to search through all recipes (including ones from other users) and save them for later
- :large_blue_circle: The app will be able to recommend new recipes based on their preferences (with similar ingredients)
- :large_blue_circle: Eventually it will be able to pull data from Spoonacular API for better recommendations


### Plans for the future: 
  * :green_circle: Secure the application using Spring Security 
  * :green_circle: Use Lombok to get rid of some boilerplate code
  * :large_blue_circle:Make a UI with React (+ maybe React Native for IOS/Android development)
<br>

### :green_circle: Update #1
Users now receive a `JWT` when registering or logging in. All other JWTs associated with the user are invalidated when a new one is generated (for example when they login). They can logout and that causes their JWTs also to be invalidated.
Users can have the role `User` or the role `Admin`, with different privileges for the Admin.
Users can only add recipes to their own accounts, since their tokens are checked and must match the id provided in the URL. For example, user with `id=12` will recieve a `403 status code` when trying to `POST` to `/api/users/13/recipes`

>More details will be added as I build the app
