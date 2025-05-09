package com.example.fakeapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.exam.fakeapi.services.driverAdapters.ProductsDriverAdapter
import com.example.fakeapi.services.driverAdapters.AuthDriverAdapter
import com.example.fakeapi.services.models.Category
import com.example.fakeapi.services.models.Product
import com.example.fakeapi.services.models.UserCredentials
import com.example.fakeapi.services.models.UserRegister
import com.example.fakeapi.ui.theme.FakeAPITheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authDriverAdapter by lazy { AuthDriverAdapter() }
    private val productsDriverAdapter by lazy { ProductsDriverAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FakeAPITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FakeApiApp(
                        authDriver = authDriverAdapter,
                        productsDriver = productsDriverAdapter
                    )
                }
            }
        }
    }
}

@Composable
fun FakeApiApp(
    authDriver: AuthDriverAdapter,
    productsDriver: ProductsDriverAdapter
) {

    var activeScreen by remember { mutableStateOf("login") }
    var isProcessing by remember { mutableStateOf(false) }
    var productsList by remember { mutableStateOf<List<Product>>(emptyList()) }
    var selectedProductItem by remember { mutableStateOf<Product?>(null) }

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var avatarInput by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }//mos men
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            //act pa
            when (activeScreen) {
                "login" -> {
                    LoginView(
                        emailValue = emailInput,
                        passwordValue = passwordInput,
                        loading = isProcessing,
                        onEmailChanged = { emailInput = it },
                        onPasswordChanged = { passwordInput = it },
                        onLoginSubmit = {
                            isProcessing = true
                            authDriver.login(
                                UserCredentials(emailInput, passwordInput),
                                onSuccess = {
                                    productsDriver.loadProducts(
                                        loadData = { items ->
                                            productsList = items
                                            isProcessing = false
                                            activeScreen = "products"
                                        },
                                        error = { errorMsg ->
                                            isProcessing = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Error cargando productos: $errorMsg")
                                            }
                                        }
                                    )
                                },
                                onError = { errorMsg ->
                                    isProcessing = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error de autenticación: $errorMsg")
                                    }
                                }
                            )
                        },
                        onRegisterNavigate = {
                            // Resetear campos para registro
                            nameInput = ""
                            avatarInput = ""
                            activeScreen = "register"
                        }
                    )
                }

                "register" -> {
                    RegisterView(
                        nameValue = nameInput,
                        emailValue = emailInput,
                        passwordValue = passwordInput,
                        avatarValue = avatarInput,
                        loading = isProcessing,
                        onNameChanged = { nameInput = it },
                        onEmailChanged = { emailInput = it },
                        onPasswordChanged = { passwordInput = it },
                        onAvatarChanged = { avatarInput = it },
                        onRegisterSubmit = {
                            isProcessing = true
                            authDriver.register(
                                UserRegister(nameInput, emailInput, passwordInput, avatarInput),
                                onSuccess = {
                                    isProcessing = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Registro exitoso")
                                    }
                                    activeScreen = "login"
                                },
                                onError = { errorMsg ->
                                    isProcessing = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error en registro: $errorMsg")
                                    }
                                }
                            )
                        },
                        onLoginNavigate = {
                            activeScreen = "login"
                        }
                    )
                }

                "products" -> {
                    ProductsView(
                        products = productsList,
                        onItemClick = { selectedProduct ->
                            selectedProductItem = selectedProduct
                            activeScreen = "product_detail"
                        },
                        onLogoutClick = {
                            // Limpiar datos de sesión
                            emailInput = ""
                            passwordInput = ""
                            productsList = emptyList()
                            activeScreen = "login"
                        }
                    )
                }

                "product_detail" -> {
                    selectedProductItem?.let { product ->
                        ProductDetailView(
                            product = product,
                            onBackClick = {
                                activeScreen = "products"
                            }
                        )
                    } ?: run {
                        // Si por alguna razón no hay producto seleccionado
                        activeScreen = "products"
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Error al cargar detalles del producto")
                        }
                    }
                }
            }

            // Overlay de carga
            if (isProcessing) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    emailValue: String,
    passwordValue: String,
    loading: Boolean,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginSubmit: () -> Unit,
    onRegisterNavigate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Inicia Sesión",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 28.dp)
        )

        OutlinedTextField(
            value = emailValue,
            onValueChange = onEmailChanged,
            label = { Text("Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !loading
        )

        OutlinedTextField(
            value = passwordValue,
            onValueChange = onPasswordChanged,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp),
            enabled = !loading
        )

        Button(
            onClick = onLoginSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = !loading && emailValue.isNotEmpty() && passwordValue.isNotEmpty()
        ) {
            Text("Acceder")
        }

        Spacer(Modifier.height(20.dp))

        TextButton(
            onClick = onRegisterNavigate,
            enabled = !loading
        ) {
            Text("¿No tienes cuenta? Crea una nueva")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    nameValue: String,
    emailValue: String,
    passwordValue: String,
    avatarValue: String,
    loading: Boolean,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onAvatarChanged: (String) -> Unit,
    onRegisterSubmit: () -> Unit,
    onLoginNavigate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = nameValue,
            onValueChange = onNameChanged,
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            enabled = !loading
        )

        OutlinedTextField(
            value = emailValue,
            onValueChange = onEmailChanged,
            label = { Text("Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            enabled = !loading
        )

        OutlinedTextField(
            value = passwordValue,
            onValueChange = onPasswordChanged,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            enabled = !loading
        )

        OutlinedTextField(
            value = avatarValue,
            onValueChange = onAvatarChanged,
            label = { Text("URL de Avatar") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            enabled = !loading
        )

        Button(
            onClick = onRegisterSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = !loading && nameValue.isNotBlank() &&
                    emailValue.isNotBlank() && passwordValue.isNotBlank() &&
                    avatarValue.isNotBlank()
        ) {
            Text("Registrarse")
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = onLoginNavigate,
            enabled = !loading
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

@Composable
fun ProductsView(
    products: List<Product>,
    onItemClick: (Product) -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CustomAppBar(
            title = { Text("Catálogo de Productos") },
            actions = {
                TextButton(onClick = onLogoutClick) {
                    Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )

        if (products.isEmpty()) {
            // Estado vacío
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No hay productos disponibles")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Intenta nuevamente más tarde", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            // Lista de productos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { item ->
                    ProductItemCard(product = item) {
                        onItemClick(item)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItemCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$ ${product.price}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = product.category?.name ?: "Sin categoría",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.description,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ProductDetailView(
    product: Product,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CustomAppBar(
            title = { Text("Detalles del Producto") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Text("←", style = MaterialTheme.typography.headlineSmall)
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Título del producto
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Imágenes del producto
                if (!product.images.isNullOrEmpty()) {
                    ProductImagesGallery(
                        images = product.images,
                        productName = product.title
                    )
                } else {
                    NoImagesPlaceholder()
                }

                // Información del producto
                ProductDetailsCard(product = product)
            }
        }
    }
}

@Composable
fun ProductImagesGallery(
    images: List<String>,
    productName: String
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images) { imageUrl ->
            Card(
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp)
            ) {
                ImageLoader(
                    imageUrl = imageUrl,
                    description = productName
                )
            }
        }
    }
}

@Composable
fun NoImagesPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay imágenes disponibles para este producto")
        }
    }
}

@Composable
fun ProductDetailsCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Precio y categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$ ${product.price}",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = product.category?.name ?: "Sin clasificación",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Descripción
            Text(
                text = "Acerca de este producto",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ImageLoader(
    imageUrl: String,
    description: String
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = description,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    ) {
        when (painter.state) {
            is coil.compose.AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(40.dp)
                    )
                }
            }
            is coil.compose.AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error al cargar imagen")
                }
            }
            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }
}

@Composable
fun CustomAppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (navigationIcon != null) {
                navigationIcon()
                Spacer(Modifier.width(8.dp))
            }

            Box(Modifier.weight(1f)) {
                title()
            }

            actions()
        }
    }
}