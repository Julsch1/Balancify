package com.example.balancify.components

import android.content.Context
import android.graphics.Paint.Align
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balancify.R
import com.example.balancify.ui.theme.DarkBlue
import com.example.balancify.ui.theme.Primary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.example.balancify.ui.theme.ButtonColor
import com.example.balancify.ui.theme.Purple80
import com.example.balancify.ui.theme.TextFieldColor
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

@Composable
fun NormalTextComponent(value: String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ), color = Color.Black,
        textAlign = TextAlign.Center
    )
}

@Composable
fun HeadingTextComponent(value: String){
    Text(
        text = value,
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        ), color = Color.Black,
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTextField(labelValue: String, imageVector : ImageVector) : String {
    val textValue = remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
        label = { Text(text = labelValue) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            focusedLabelColor =  Color.Black,
            cursorColor = Color.Black,
            containerColor = TextFieldColor

        ),
        keyboardOptions = KeyboardOptions.Default,
        value = textValue.value,
        onValueChange = {
            textValue.value = it
        },
        leadingIcon = { Icon(imageVector = imageVector, contentDescription = imageVector.name)},
    )
    return textValue.value
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(labelValue: String, imageVector : ImageVector) : String{
    val password = remember {
        mutableStateOf("")
    }

    val passwordVisible = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            focusedLabelColor =  Color.Black,
            cursorColor = Color.Black,
            containerColor = TextFieldColor

        ),
        keyboardOptions  = KeyboardOptions(keyboardType = KeyboardType.Password),
        value = password.value,
        onValueChange = {
            password.value = it
        },
        leadingIcon = { Icon(imageVector = imageVector, contentDescription = imageVector.name)},
        trailingIcon = {
            val iconImage = if(passwordVisible.value){
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }

            var description = if (passwordVisible.value){
                stringResource(id = R.string.hide_pwd)

            } else{
                stringResource(id = R.string.hide_pwd)
            }

            IconButton(onClick = { passwordVisible.value = !passwordVisible.value}) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        visualTransformation = if(passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
    )

    return password.value
}

@Composable
fun CheckBoxComponent(value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(40.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val checkedState = remember {
            mutableStateOf<Boolean>(false)
        }

        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
            },
            modifier = Modifier.size(40.dp) // Adjusted checkbox size to match text height
        )

        Text(
            text = value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
                .alpha(0.8f),
            style = TextStyle(
                fontSize = 14.sp, // Adjusted text size
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            ),
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = Int.MAX_VALUE, // Allow unlimited lines
            overflow = TextOverflow.Ellipsis,
            softWrap = true // Enable soft wrapping
        )
    }
}
@Composable
fun RegisterButtonComponent(
    first_name: String,
    last_name: String,
    email: String,
    password: String,
    navController: NavController,
    context: Context
) {
    val auth = FirebaseAuth.getInstance()

    Button(
        onClick = {
            // Validate input fields
            if (first_name.isEmpty() || last_name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@Button
            }

            // Perform Firebase authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Registration successful, set display name and navigate to home screen
                        val user = auth.currentUser

                        // Create UserProfileChangeRequest
                        val profileUpdates = userProfileChangeRequest {
                            displayName = "$first_name"
                        }

                        // Update user profile
                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateProfileTask ->
                                if (updateProfileTask.isSuccessful) {
                                    // Successfully updated profile
                                    navController.navigate("home")
                                } else {
                                    // Failed to update profile, handle the error
                                    Toast.makeText(
                                        context,
                                        "Failed to update profile: ${updateProfileTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        // Registration failed, show an error message or handle the error appropriately
                        Toast.makeText(
                            context,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        },
        modifier = Modifier.fillMaxWidth().widthIn(max = 24.dp).heightIn(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor,
            contentColor = Color.White
        )
    ) {
        Text(text = "Register")
    }
}

//@Composable
//fun RegisterButtonComponent(first_name: String, last_name : String, email: String, password: String,  navController: NavController, context: Context){
//    var auth: FirebaseAuth = Firebase.auth
//
//    Button(
//        onClick = {
//            // Validate input fields
//            if (first_name.isEmpty() || last_name.isEmpty() || email.isEmpty() || password.isEmpty()) {
//                // Show an error message or handle validation appropriately
////                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
//                return@Button
//            }
//
//            // Perform Firebase authentication
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        // Registration successful, navigate to home screen
//                        navController.navigate("home")
//                    } else {
//                        navController.navigate("goals")
//                        // Registration failed, show an error message or handle the error appropriately
////                        Toast.makeText(
////                            context,
////                            "Registration failed: ${task.exception?.message}",
////                            Toast.LENGTH_SHORT
////                        ).show()
//                    }
//                }
//        },
//        modifier = Modifier.fillMaxWidth().widthIn(max = 24.dp).heightIn(40.dp),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = ButtonColor,
//            contentColor = Color.White
//        )
//    ) {
//        Text(text = "Register")
//    }
//}

@Composable
fun ToLoginButtonComponent(name: String, navController: NavController) {

    Button(
        onClick = {
            navController.navigate("login")
        },
        modifier = Modifier.fillMaxWidth().widthIn(max = 10.dp).heightIn(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor,
            contentColor = Color.White
        )
    ) {
        Text(text = "Login")
    }
}

@Composable
fun LoginButtonComponent(email: String, password: String, navController: NavController, context: Context) {
    val auth = FirebaseAuth.getInstance()
    Button(
        onClick = {
            // Validate email and password
            if (email.isEmpty() || password.isEmpty()) {
                // Show an error message or handle validation appropriately
                Toast.makeText(context, "Email and password are required", Toast.LENGTH_SHORT).show()
                return@Button
            }

            // Perform Firebase authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Login successful, navigate to home screen
                        navController.navigate("home")
                    } else {
                        // Login failed, show an error message or handle the error appropriately
                        Toast.makeText(
                            context,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        },
        modifier = Modifier.fillMaxWidth().widthIn(max = 10.dp).heightIn(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor,
            contentColor = Color.White
        )
    ) {
        Text(text = "Login")
    }
}





@Composable
fun DividerTextComponent(){
    Row(modifier = Modifier.fillMaxWidth()
        , verticalAlignment = Alignment.CenterVertically){

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color.Black,
            thickness = 1.dp
        )

        Text(//modifier = Modifier.padding(8.dp),
            text = "or",
            fontSize = 14.sp,
            color = Color.Black)

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color.Black,
            thickness = 1.dp
        )
    }
}

@Composable
fun GoogleLoginButton(navController: NavController){
    Button(
        onClick = {

            navController.navigate("home")

        },
        modifier = Modifier.fillMaxWidth().widthIn(max = 24.dp).heightIn(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor,
            contentColor = Color.White
        )
        ) {
        Text(text = "Login with Google")
    }
}

@Composable
fun GoToRegisterButton(navController: NavController){
    Button(
        onClick = {
            navController.navigate("register")
        },
        modifier = Modifier.fillMaxWidth().widthIn(max = 24.dp).heightIn(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor,
            contentColor = Color.White
        )
    ) {
        Text(text = "Register")
    }
}

@Composable
fun SeparationBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(color = colorResource(id = R.color.dark_blue))
    ) {
        // Text inside the separation box
        Text(
            text = text,
            modifier = Modifier
                .background(color = colorResource(id = R.color.dark_blue)),
            color = Color.White
        )
    }
}

@Composable
fun RoundButton(icon: ImageVector, navigateTo: String, navController: NavController) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color = colorResource(id = R.color.main_blue), shape = CircleShape)
            .clickable {
                navController.navigate(navigateTo)
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun AddButton() {
    IconButton(
        onClick = { /* Add button click action */ },
        modifier = Modifier
            .size(40.dp)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add"
        )
    }
}
