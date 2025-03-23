package com.example.navalbattle

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.navalbattle.ui.theme.NavalBattleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    // Inicializa os ViewModels
    private val loginViewModel: LoginViewModel by viewModels()
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inicializa o SensorManager e o sensor de luz
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            // Estado para alternar entre tema claro e escuro
            var isLightTheme by remember { mutableStateOf(true) }

            // Listener para o sensor de luz
            val lightSensorListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                        val lux = event.values[0] // Nível de luz em lux
                        isLightTheme = lux > 50 // Alterna para tema claro se a luz for maior que 50 lux
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            // Registra o listener do sensor de luz
            LaunchedEffect(Unit) {
                lightSensor?.also { light ->
                    sensorManager.registerListener(
                        lightSensorListener,
                        light,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
            }

            // Define o tema personalizado com base no estado
            NavalBattleTheme(isLightTheme = isLightTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Botão para alternar tema manualmente (opcional, para teste)
                        Button(
                            onClick = { isLightTheme = !isLightTheme },
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLightTheme) Color(0xFF90A4AE) else Color(0xFF0288D1),
                                contentColor = if (isLightTheme) Color.Black else Color.White
                            )
                        ) {
                            Text(
                                text = if (isLightTheme) "Switch to Dark Theme" else "Switch to Light Theme",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Configura a navegação
                        NavHost(navController = navController, startDestination = "login") {
                            composable("login") { LoginScreen(auth, navController, isLightTheme, loginViewModel) }
                            composable("game") { GameScreen(isLightTheme, gameViewModel, navController) }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Desregistra o listener do sensor quando a atividade é pausada
        sensorManager.unregisterListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {}
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        })
    }
}

@Composable
fun LoginScreen(auth: FirebaseAuth, navController: androidx.navigation.NavController, isLightTheme: Boolean, viewModel: LoginViewModel) {
    // Estado para controlar a rolagem
    val scrollState = rememberScrollState()

    // Fundo com gradiente (claro ou escuro)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isLightTheme) listOf(Color.White, Color(0xFFE0E0E0))
                    else listOf(Color(0xFF003087), Color(0xFF4FC3F7))
                )
            )
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagem acima do campo de email
        Image(
            painter = painterResource(id = R.drawable.intro),
            contentDescription = "Email Logo",
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 32.dp)
        )

        // Campo de email
        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = {
                viewModel.email.value = it
                viewModel.emailError.value = it.isEmpty()
            },
            label = { Text("Enter your email", color = if (isLightTheme) Color.Black else Color.White) },
            isError = viewModel.emailError.value,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            textStyle = TextStyle(color = if (isLightTheme) Color.Black else Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isLightTheme) Color(0xFF90A4AE) else Color(0xFF4FC3F7),
                unfocusedBorderColor = if (isLightTheme) Color(0xFFB0BEC5) else Color.White.copy(alpha = 0.5f),
                errorBorderColor = Color.Red,
                focusedContainerColor = if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF1E3A8A).copy(alpha = 0.8f),
                unfocusedContainerColor = if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF1E3A8A).copy(alpha = 0.5f),
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de senha
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = {
                viewModel.password.value = it
                viewModel.passwordError.value = it.isEmpty()
            },
            label = { Text("Enter your password", color = if (isLightTheme) Color.Black else Color.White) },
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.passwordError.value,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            textStyle = TextStyle(color = if (isLightTheme) Color.Black else Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isLightTheme) Color(0xFF90A4AE) else Color(0xFF4FC3F7),
                unfocusedBorderColor = if (isLightTheme) Color(0xFFB0BEC5) else Color.White.copy(alpha = 0.5f),
                errorBorderColor = Color.Red,
                focusedContainerColor = if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF1E3A8A).copy(alpha = 0.8f),
                unfocusedContainerColor = if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF1E3A8A).copy(alpha = 0.5f),
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de login
        Button(
            onClick = {
                viewModel.emailError.value = viewModel.email.value.isEmpty()
                viewModel.passwordError.value = viewModel.password.value.isEmpty()
                if (!viewModel.emailError.value && !viewModel.passwordError.value) {
                    auth.signInWithEmailAndPassword(viewModel.email.value, viewModel.password.value)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModel.clearUserData() // Limpa os dados do usuário
                                navController.navigate("game") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                viewModel.errorMessage.value = task.exception?.message
                            }
                        }
                } else {
                    viewModel.errorMessage.value = "Please fill in all fields!"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLightTheme) Color(0xFF90A4AE) else Color(0xFF0288D1),
                contentColor = if (isLightTheme) Color.Black else Color.White
            )
        ) {
            Text("Login", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de registro
        Button(
            onClick = {
                viewModel.emailError.value = viewModel.email.value.isEmpty()
                viewModel.passwordError.value = viewModel.password.value.isEmpty()
                if (!viewModel.emailError.value && !viewModel.passwordError.value) {
                    auth.createUserWithEmailAndPassword(viewModel.email.value, viewModel.password.value)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModel.errorMessage.value = "User registered successfully!"
                            } else {
                                viewModel.errorMessage.value = task.exception?.message
                            }
                        }
                } else {
                    viewModel.errorMessage.value = "Please fill in all fields!"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLightTheme) Color(0xFF90A4AE) else Color(0xFF0288D1),
                contentColor = if (isLightTheme) Color.Black else Color.White
            )
        ) {
            Text("Register", style = MaterialTheme.typography.bodyLarge)
        }

        // Mensagem de erro ou sucesso
        viewModel.errorMessage.value?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = if (it.contains("successfully")) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun GameScreen(isLightTheme: Boolean, viewModel: GameViewModel, navController: androidx.navigation.NavController) {
    val db = FirebaseFirestore.getInstance()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown User"
    val scaffoldState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cellSize = (screenWidth - 64.dp) / 10
    val scope = rememberCoroutineScope()

    // Estado para controlar a rolagem
    val scrollState = rememberScrollState()

    // Pontuação do jogador e da IA
    val playerScore by remember(viewModel.gameState.value.moves) {
        mutableStateOf(viewModel.gameState.value.moves.count { it.result == CellState.HIT && it.isPlayerMove })
    }
    val aiScore by remember(viewModel.gameState.value.moves) {
        mutableStateOf(viewModel.gameState.value.moves.count { it.result == CellState.HIT && !it.isPlayerMove })
    }

    // Atualiza o estado dos navios (marca como afundado)
    LaunchedEffect(viewModel.gameState.value.board) {
        viewModel.gameState.value.ships.forEach { ship ->
            if (!ship.isSunk) {
                val shipCells = mutableListOf<Pair<Int, Int>>()
                for (i in viewModel.gameState.value.board.indices) {
                    for (j in viewModel.gameState.value.board[i].indices) {
                        if (viewModel.gameState.value.board[i][j] == CellState.SHIP || viewModel.gameState.value.board[i][j] == CellState.HIT) {
                            shipCells.add(i to j)
                        }
                    }
                }
                val hitCount = shipCells.count { (i, j) -> viewModel.gameState.value.board[i][j] == CellState.HIT }
                if (shipCells.isNotEmpty() && hitCount == ship.size) {
                    ship.isSunk = true
                }
            }
        }
    }

    // Conta os navios restantes
    val remainingShips by remember(viewModel.gameState.value.ships) {
        mutableStateOf(viewModel.gameState.value.ships.count { !it.isSunk })
    }

    // Verificar o vencedor (quando todos os navios forem afundados)
    val winner by remember(remainingShips) {
        mutableStateOf(
            if (remainingShips == 0) {
                if (viewModel.gameState.value.lastSunkByPlayer) "Player" else "AI"
            } else {
                null
            }
        )
    }

    // Layout principal com Box para sobrepor o Snackbar
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isLightTheme) listOf(Color.White, Color(0xFFE0E0E0))
                    else listOf(Color(0xFF003087), Color(0xFF4FC3F7))
                )
            )
    ) {
        // Conteúdo rolável
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Email do jogador
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Player: $userEmail",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isLightTheme) Color.Black else Color.White
                )

                // Botão de logout (discreto)
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isLightTheme) Color(0xFFEF5350) else Color(0xFFD32F2F),
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = if (isLightTheme) Color(0xFFEF5350) else Color(0xFFD32F2F),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable {
                            viewModel.resetGame() // Reinicia o jogo
                            FirebaseAuth.getInstance().signOut() // Desconecta o usuário
                            navController.navigate("login") {
                                popUpTo("game") { inclusive = true } // Limpa a pilha de navegação
                            }
                        }
                )
            }

            // Exibir pontuações
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF1E3A8A).copy(alpha = 0.5f)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Player: $playerScore",
                    color = if (isLightTheme) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "AI: $aiScore",
                    color = if (isLightTheme) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Remaining Ships: $remainingShips",
                    color = if (isLightTheme) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tabuleiro de ataque (contra a IA)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = if (isLightTheme) listOf(Color(0xFFCFD8DC), Color(0xFFE0E0E0))
                            else listOf(Color(0xFF4FC3F7), Color(0xFF0288D1))
                        )
                    )
                    .border(2.dp, if (isLightTheme) Color(0xFFB0BEC5) else Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                for (i in 0 until 10) {
                    Row {
                        for (j in 0 until 10) {
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .border(1.dp, if (isLightTheme) Color(0xFFB0BEC5) else Color.White.copy(alpha = 0.3f))
                                    .background(
                                        when (viewModel.gameState.value.board[i][j]) {
                                            CellState.HIT -> Color(0xFFFF6F61).copy(alpha = 0.9f)
                                            CellState.MISS -> Color(0xFFB0BEC5).copy(alpha = 0.9f)
                                            else -> if (isLightTheme) Color(0xFFCFD8DC).copy(alpha = 0.9f)
                                            else Color(0xFFBBDEFB).copy(alpha = 0.9f)
                                        }
                                    )
                                    .clickable {
                                        if (viewModel.gameState.value.board[i][j] == CellState.EMPTY) {
                                            viewModel.gameState.value = viewModel.gameState.value.copy(
                                                board = viewModel.gameState.value.board.copyOf().apply { this[i][j] = CellState.MISS },
                                                moves = viewModel.gameState.value.moves.apply { add(Move(i, j, CellState.MISS, true)) },
                                                playerTurn = false
                                            )
                                        } else if (viewModel.gameState.value.board[i][j] == CellState.SHIP) {
                                            val newBoard = viewModel.gameState.value.board.copyOf().apply { this[i][j] = CellState.HIT }
                                            val newMoves = viewModel.gameState.value.moves.apply { add(Move(i, j, CellState.HIT, true)) }
                                            // Verifica se o navio foi afundado
                                            val shipCells = mutableListOf<Pair<Int, Int>>()
                                            for (row in newBoard.indices) {
                                                for (col in newBoard[row].indices) {
                                                    if (newBoard[row][col] == CellState.SHIP || newBoard[row][col] == CellState.HIT) {
                                                        shipCells.add(row to col)
                                                    }
                                                }
                                            }
                                            val hitCount = shipCells.count { (r, c) -> newBoard[r][c] == CellState.HIT }
                                            val ship = viewModel.gameState.value.ships.find { it.size == shipCells.size && !it.isSunk }
                                            val isSunk = ship != null && hitCount == ship.size
                                            if (isSunk) {
                                                ship?.isSunk = true
                                            }
                                            viewModel.gameState.value = viewModel.gameState.value.copy(
                                                board = newBoard,
                                                moves = newMoves,
                                                lastSunkByPlayer = isSunk,
                                                playerTurn = false
                                            )
                                        }
                                    }
                            ) {
                                when (viewModel.gameState.value.board[i][j]) {
                                    CellState.HIT -> Text(
                                        "X",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                    CellState.MISS -> Text(
                                        "O",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão para reiniciar o jogo
            Button(
                onClick = {
                    viewModel.gameState.value = GameState()
                    placeShips(viewModel.gameState.value.board, viewModel.gameState.value.ships)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLightTheme) Color(0xFF90A4AE) else Color(0xFF0288D1),
                    contentColor = if (isLightTheme) Color.Black else Color.White
                )
            ) {
                Text("Restart Game", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para salvar partida
            Button(
                onClick = { viewModel.showSaveDialog.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLightTheme) Color(0xFF90A4AE) else Color(0xFF0288D1),
                    contentColor = if (isLightTheme) Color.Black else Color.White
                )
            ) {
                Text("Save Game", style = MaterialTheme.typography.bodyLarge)
            }

            // Diálogo para salvar partida
            if (viewModel.showSaveDialog.value) {
                AlertDialog(
                    onDismissRequest = { viewModel.showSaveDialog.value = false },
                    title = { Text("Confirm Save", style = MaterialTheme.typography.headlineSmall) },
                    text = { Text("Do you want to save this game?", style = MaterialTheme.typography.bodyLarge) },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                                    if (userId != null) {
                                        val gameData = hashMapOf(
                                            "moves" to viewModel.gameState.value.moves,
                                            "playerScore" to playerScore,
                                            "aiScore" to aiScore,
                                            "winner" to (winner ?: "None"),
                                            "winnerScore" to if (winner != null) (if (viewModel.gameState.value.lastSunkByPlayer) playerScore else aiScore) else 0,
                                            "timestamp" to System.currentTimeMillis()
                                        )
                                        db.collection("games")
                                            .document(userId)
                                            .set(gameData)
                                            .addOnSuccessListener {
                                                scope.launch {
                                                    scaffoldState.showSnackbar("Game saved successfully! Winner: ${winner ?: "None"} with ${(if (viewModel.gameState.value.lastSunkByPlayer) playerScore else aiScore)} points.")
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                scope.launch {
                                                    scaffoldState.showSnackbar("Error saving game: $e")
                                                }
                                            }
                                    } else {
                                        scope.launch {
                                            scaffoldState.showSnackbar("User not authenticated!")
                                        }
                                    }
                                }
                                viewModel.showSaveDialog.value = false
                            },
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLightTheme) Color(0xFF90A4AE) else Color(0xFF0288D1),
                                contentColor = if (isLightTheme) Color.Black else Color.White
                            )
                        ) {
                            Text("Yes", style = MaterialTheme.typography.bodyLarge)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { viewModel.showSaveDialog.value = false },
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLightTheme) Color(0xFF90A4AE) else Color(0xFF0288D1),
                                contentColor = if (isLightTheme) Color.Black else Color.White
                            )
                        ) {
                            Text("No", style = MaterialTheme.typography.bodyLarge)
                        }
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Snackbar para feedback, fixo na parte inferior
        SnackbarHost(
            hostState = scaffoldState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    // Turno da IA
    if (!viewModel.gameState.value.playerTurn) {
        LaunchedEffect(Unit) {
            val (row, col) = aiTurn(viewModel.gameState.value.board)
            val newBoard = viewModel.gameState.value.board.copyOf().apply {
                this[row][col] = if (this[row][col] == CellState.SHIP) CellState.HIT else CellState.MISS
            }
            val newMoves = viewModel.gameState.value.moves.apply {
                add(Move(row, col, if (viewModel.gameState.value.board[row][col] == CellState.SHIP) CellState.HIT else CellState.MISS, false))
            }
            // Verifica se o navio foi afundado pela IA
            val shipCells = mutableListOf<Pair<Int, Int>>()
            for (i in newBoard.indices) {
                for (j in newBoard[i].indices) {
                    if (newBoard[i][j] == CellState.SHIP || newBoard[i][j] == CellState.HIT) {
                        shipCells.add(i to j)
                    }
                }
            }
            val hitCount = shipCells.count { (r, c) -> newBoard[r][c] == CellState.HIT }
            val ship = viewModel.gameState.value.ships.find { it.size == shipCells.size && !it.isSunk }
            val isSunk = ship != null && hitCount == ship.size
            if (isSunk) {
                ship?.isSunk = true
            }
            viewModel.gameState.value = viewModel.gameState.value.copy(
                board = newBoard,
                moves = newMoves,
                lastSunkByPlayer = !isSunk,
                playerTurn = true
            )
        }
    }
}

fun placeShips(board: Array<Array<CellState>>, ships: List<Ship>) {
    ships.forEach { ship ->
        var placed = false
        while (!placed) {
            val orientation = listOf(0, 1).random()
            val row = (0..9).random()
            val col = (0..9).random()

            if (canPlaceShip(board, row, col, ship.size, orientation)) {
                for (i in 0 until ship.size) {
                    if (orientation == 0) board[row][col + i] = CellState.SHIP
                    else board[row + i][col] = CellState.SHIP
                }
                placed = true
            }
        }
    }
}

fun canPlaceShip(board: Array<Array<CellState>>, row: Int, col: Int, size: Int, orientation: Int): Boolean {
    if (orientation == 0 && col + size > 10) return false
    if (orientation == 1 && row + size > 10) return false
    for (i in 0 until size) {
        val r = if (orientation == 0) row else row + i
        val c = if (orientation == 0) col + i else col
        if (board[r][c] != CellState.EMPTY) return false
    }
    return true
}

fun aiTurn(board: Array<Array<CellState>>): Pair<Int, Int> {
    while (true) {
        val row = (0..9).random()
        val col = (0..9).random()
        if (board[row][col] == CellState.EMPTY || board[row][col] == CellState.SHIP) {
            return row to col
        }
    }
}