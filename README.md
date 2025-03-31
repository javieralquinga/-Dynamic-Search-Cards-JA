# JA Developer – Dynamic Search Cards (AEM + RAWG Integration)

Este proyecto implementa un componente completo en **Adobe Experience Manager (AEM)** que consume una API externa (RAWG.io) para mostrar tarjetas dinámicas filtrables con búsqueda en tiempo real.

##  Características principales

- Componente AEM llamado `dynamic-search-cards`
- Muestra tarjetas con:
  - Imagen destacada
  - Título
  - Descripción
  - Tags (categorías)
- Filtro por tags
- Buscador en tiempo real
- Conexión a la API externa [RAWG](https://rawg.io/apidocs)
- Service OSGi configurable por entorno (`dev`, `stage`, `prod`)
- Pruebas unitarias con JUnit5 y wcm.io AEM Mock

##  Arquitectura del Proyecto

```
ja-developer/
├── core/
│   ├── src/main/java/com.jadeveloper.core/
│   │   ├── models/
│   │   ├── services/
│   │   ├── services/impl/
│   │   └── servlets/
│   ├── src/test/java/com.jadeveloper.core/
│   │   ├── services/impl/
│   │   └── servlets/
│   └── pom.xml
├── ui.apps/
│   └── apps/ja-developer/components/dynamic-search-cards/
│       ├── component.html
│       ├── _cq_dialog/.content.xml
│       ├── clientlibs/
│       │   ├── js/
│       │   ├── css/
│       └── README.md
└── ui.config/
    └── config.dev/
    └── config.stage/
    └── config.prod/
```

## ️ Instalación y Despliegue

```bash
git clone https://github.com/javieralquinga/-Dynamic-Search-Cards-JA.git
cd ja-developer
```

Configura las variables en `/ui.config/src/main/content/jcr_root/apps/ja-developer/config.<env>/com.jadeveloper.core.services.impl.ExternalApiServiceImpl.cfg.json`

Ejemplo:
```json
{
  "endpointUrl": "https://api.rawg.io/api/games?key=TU_API_KEY&dates=2024-01-01,2024-12-31&platforms=18,1,7",
  "timeout": 5000
}
```

```bash
mvn clean install -PautoInstallSinglePackage
```

##  Componente: Dynamic Search Cards

- Ruta: `/apps/ja-developer/components/dynamic-search-cards`
- Dialog: título, ruta de búsqueda, endpoint externo
- HTML Sightly + JS dinámico + clientlib
- CSS adaptable

##  Pruebas

Ubicación: `core/src/test/java/com.jadeveloper.core/`

| Archivo | Tipo de prueba | Descripción |
|---------|----------------|-------------|
| `ExternalApiServiceImplTest` | Unit | API externa |


## 📚 Dependencias

- `sling.api`, `engine`, `models.impl`
- `osgi.annotations`
- `wcm.io AEM Mock`
- `JUnit 5`, `Mockito`

## 🔄 Configuración por Entorno

- `config.dev/`
- `config.stage/`
- `config.prod/`

---

Javier Alquinga
