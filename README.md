# ArquTechSolutions 📱

Este proyecto es una aplicación móvil Android desarrollada para **ArquTechSolutions**, una empresa especializada en soluciones arquitectónicas innovadoras que combinan diseño estético con tecnología de vanguardia.

## 🚀 Características

- **Aplicación nativa Android** con Kotlin y XML
- **Diseño moderno** y profesional para el sector arquitectónico
- **Sistema de autenticación** completo (login/registro)
- **Tienda integrada** con carrito de compras
- **Sistema de pagos** simulado
- **Gestión de pedidos** e historial
- **Multidioma** (Español, Inglés, Portugués)
- **Interfaz responsiva** para diferentes dispositivos

## 🛠️ Tecnologías Utilizadas

- **Kotlin** - Lenguaje de programación principal
- **XML** - Diseño de interfaces de usuario
- **Android Studio** - Entorno de desarrollo oficial
- **Corrutinas** - Programación asíncrona
- **SharedPreferences** - Almacenamiento local
- **Material Design** - Componentes de UI modernos
- **ViewPager2** - Sliders y carruseles
- **RecyclerView** - Listas eficientes

## 📁 Estructura del Proyecto

```
ArquTechSolutions/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/myapplicationarturocashfaster/
│   │   │   ├── activities/          # Actividades principales
│   │   │   ├── adapters/            # Adaptadores para RecyclerView
│   │   │   ├── data/               # Modelos de datos
│   │   │   └── managers/           # Gestores de datos
│   │   ├── res/
│   │   │   ├── layout/             # Archivos XML de layout
│   │   │   ├── drawable/           # Recursos gráficos e iconos
│   │   │   ├── values/             # Strings, colores, estilos
│   │   │   └── menu/               # Menús de navegación
│   │   └── AndroidManifest.xml
└── build.gradle.kts
```

## 🎯 Módulos Principales

### 🔐 Autenticación
- `LoginActivity` - Inicio de sesión de usuarios
- `RegisterActivity` - Registro de nuevos usuarios
- `SupabaseManager` - Gestión de autenticación con Supabase

### 🏠 Principal
- `MainActivity` - Pantalla principal con sliders
- `BaseActivity` - Actividad base con soporte multidioma

### 🛍️ Tienda y Compras
- `StoreActivity` - Catálogo de productos
- `CartActivity` - Gestión del carrito
- `CartManager` - Gestor del carrito de compras
- `CheckoutActivity` - Proceso de pago
- `PaymentSuccessActivity` - Confirmación de pago

### 📦 Gestión de Pedidos
- `OrderHistoryActivity` - Historial de pedidos
- `OrderDetailActivity` - Detalle de pedido
- `OrderManager` - Gestor de órdenes
- `OrdersAdapter` - Adaptador para lista de pedidos

### ⚙️ Utilidades
- `LocaleHelper` - Gestión de idiomas
- `FavoritesManager` - Gestión de favoritos
- `ApiService` - Servicio simulado de pagos

## 🎨 Características Técnicas

### Arquitectura
- **Programación reactiva** con corrutinas
- **Patrón Manager** para gestión de datos
- **SharedPreferences** para persistencia local
- **Separación de responsabilidades** en módulos

### UI/UX
- **Material Design 3** - Componentes modernos
- **ViewPager2** - Sliders automáticos
- **RecyclerView** - Listas optimizadas
- **Navegación intuitiva** entre actividades

### Gestión de Datos
```kotlin
// Ejemplo de modelo de datos
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val isForRent: Boolean,
    val rentPricePerDay: Double?,
    val stock: Int
)

data class CartItem(
    val product: Product, 
    var quantity: Int
)
```

## 🚀 Instalación y Uso

1. **Clona el repositorio:**
   ```bash
   git clone https://github.com/ChrisBaroneYT/ArquTechSolutions.git
   ```

2. **Abre el proyecto en Android Studio:**
   - Abre Android Studio
   - Selecciona "Open an existing project"
   - Navega a la carpeta del proyecto

3. **Configura el entorno:**
   - Android Studio Arctic Fox o superior
   - SDK de Android actualizado
   - Dispositivo virtual o físico con Android 8.0+

4. **Ejecuta la aplicación:**
   - Conecta un dispositivo o inicia un emulador
   - Haz clic en "Run" ▶️ para compilar y ejecutar

## 📱 Funcionalidades Destacadas

### 🛒 Sistema de Carrito
- Agregar/remover productos
- Modificar cantidades
- Cálculo automático de subtotal, IVA y total
- Persistencia local de datos

### 💳 Proceso de Pago
- Validación de formularios
- Simulación de procesamiento de pago
- Gestión de estados de pedidos
- Confirmación de transacciones

### 🌐 Soporte Multidioma
- Español, Inglés, Portugués
- Cambio dinámico de idioma
- Persistencia de preferencias

## 🔧 Configuración
### Dependencias Principales (build.gradle.kts)
```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("com.google.code.gson:gson:2.10.1")
}
```

### Permisos (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 🤝 Contribución

Las contribuciones son bienvenidas. Para contribuir:

1. Haz un fork del proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 👥 Autor
- **Christian Barone** - [ChrisBaroneYT](https://github.com/ChrisBaroneYT)

## 📞 Contacto
- 📧 Email: co.cristiand@gmail.com

**ArquTechSolutions** - Donde la arquitectura se encuentra con la innovación tecnológica móvil.
