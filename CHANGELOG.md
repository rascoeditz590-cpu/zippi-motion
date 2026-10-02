# CHANGELOG - Zippi Motion

All notable changes, architectural milestones, and module contracts across phases are documented here.

## [Phase 0] - Foundation, Schema & Design System (2026-10-02)

### Added
- **Application Identity & Branding**:
  - Configured project as "Zippi Motion".
  - Created custom adaptive launcher icon with neon pink (`#FF2A85`) to radiant amber (`#FFAA00`) dynamic Z-ribbon on dark obsidian (`#12111A`) background.
  - Set unique `applicationId` to `com.aistudio.zippimotion.krvpxt` with `minSdk = 26`.
  - Configured ABI splits (`arm64-v8a`, `armeabi-v7a`, `x86_64`) for release size optimization.
- **Design System & Studio Theme**:
  - Implemented `ZippiMotionTheme` with calibrated dark studio backgrounds (`#0D0C13`, `#16151F`, `#211F2D`).
  - Added primary neon pink (`#FF2A85`) and electric amber (`#FFAA00`) gradients, timeline track colors, and keyframe diamond tokens.
  - Built custom reusable components: `ZippiTopBar`, `ZippiGradientButton`, `AspectRatioBadge`.
- **Room Database Persistence**:
  - `ProjectEntity`: id, name, aspect ratio (16:9, 9:16, 1:1, 4:5, 4:3, 3:4), width, height, fps (24, 30, 60, 120), durationMs, backgroundColor, timestamps, favorite status.
  - `LayerEntity`: id, projectId (foreign key CASCADE), name, layerType (VIDEO, IMAGE, AUDIO, TEXT, SHAPE, NULL, GROUP, ADJUSTMENT), trackIndex, startTimeMs, endTimeMs, speed, volume, parentId, blendMode, maskType, maskFeather, maskInvert, typography, vector shape styling, and JSON effects chain.
  - `KeyframeEntity`: id, layerId (foreign key CASCADE), property (POSITION_X, POSITION_Y, SCALE_X, ROTATION_Z, TILT_X, OPACITY, etc.), timeMs, value, easingType (15+ curves), and cubic bezier parameters.
  - Relational POJOs: `ProjectWithLayers`, `LayerWithKeyframes`.
  - DAOs: `ProjectDao`, `LayerDao`, `KeyframeDao` with Flow queries and reactive transactions.
- **Repositories & DI**:
  - `ProjectRepository` / `ProjectRepositoryImpl`: Project CRUD, project duplication/cloning with all layers and keyframes, layer/keyframe manipulation, initial starter project seeding.
  - `DeviceCapabilityRepository`: Hardware encoder detection via `MediaCodecList` (dynamically verifies 24/30/60/120 fps and 4K encoder availability).
  - `UserPreferencesRepository`: DataStore preferences for auth session, proxy workflow toggle, and magnetic timeline snapping.
  - `AppContainer` & `DefaultAppContainer`: Dependency injection container providing singletons to ViewModels.
  - `AppViewModelFactory`: Factory for `HomeViewModel`, `NewProjectViewModel`, `EditorViewModel`, `AuthViewModel`, `ProfileViewModel`, `ExportViewModel`, `AiChatViewModel`.
- **Navigation & Screens**:
  - Animated Jetpack Compose `NavHost` connecting 7 destinations:
    1. `HomeScreen`: Project list with aspect ratio badges, search, favorite filtering, duplication, deletion, and quickstart hero action.
    2. `NewProjectScreen`: Aspect ratios, encoder-supported frame rates, resolution presets, duration, and canvas background picker.
    3. `EditorScreen`: CapCut-style timeline UX combined with Alight Motion layer/keyframe architecture, canvas preview, playback controls, timecode, and tabbed workspace (Timeline, Inspector, Keyframes, Shaders).
    4. `AuthScreen`: Login, Signup, and guest mode.
    5. `ProfileScreen`: Creator profile, proxy workflow toggle, timeline snapping toggle, cache management, and OFL open-licensing compliance.
    6. `ExportScreen`: 720p - 4K resolution, H.264/H.265 codecs, bitrate slider, estimated file size, and render progression.
    7. `AiChatScreen`: AI motion graphics assistant with suggested prompt chips and backend conversation interface.

## [Phase 1] - Vector Shapes Engine (40+ Shapes), Media Import & Canvas Renderer (2026-10-02)

### Added
- **Vector Shapes Engine (`VectorShapePathBuilder`)**:
  - Implemented mathematical Compose `Path` generation for all 41 vector shapes in `ShapeType`:
    - Geometric: Rectangle, Rounded Rect, Circle, Ellipse, Triangle, Right Triangle, Diamond, Capsule, Ring, Parallelogram, Trapezoid.
    - Polygons: Pentagon, Hexagon, Octagon (regular polygonal trigonometric paths).
    - Stars: 4-Point, 5-Point, 6-Point, 8-Point Stars, and 12-Point Burst with customizable inner/outer radii.
    - Symbols: Heart (cubic bezier lobes), Cross, Crescent Moon (PathOperation.Difference), Sun (radial rays), Lightning Bolt, Cloud, Flower (6-petal radial), Gear (10-tooth with inner bore), Checkmark.
    - Arrows: Arrow Right, Left, Up, Down, Double Arrow, Chevron Right.
    - Callouts: Speech Bubble, Thought Bubble, Rectangular Callout.
    - Badges: Banner Ribbon (fishtail cuts), Shield (tapered cubic curves), Hexagonal Badge, Price Tag.
  - `VectorShapeView`: Composable supporting custom vector paths, fill colors, and stroke boundaries.
- **Media & Layer Import Pipeline**:
  - `AddLayerBottomSheet`: Complete modal bottom sheet offering creation for all 8 layer types:
    - Video: Android Photo Picker (`ActivityResultContracts.PickVisualMedia` with `VideoOnly`).
    - Image: Android Photo Picker (`ActivityResultContracts.PickVisualMedia` with `ImageOnly`).
    - Audio: Document/Media Picker (`ActivityResultContracts.GetContent` with `"audio/*"`).
    - Text: `AddTextLayerDialog` with live preview, font size slider (18-110sp), and studio color palette.
    - Shape: `ShapePickerDialog` with 7 category tabs ("Geometric", "Polygons", "Stars", "Symbols", "Arrows", "Callouts", "Badges"), live vector thumbnails, and 8-color neon fill selector.
    - Null Controller: Invisible layer for parenting transforms and grouping motion paths.
    - Adjustment Layer: Tint/Shader adjustment layer.
    - Group Container: Composite multi-track layer container.
- **Live Canvas Layer Renderer (`CanvasLayerRenderer`)**:
  - Real-time video preview viewport rendering multi-track layers at any given `playheadMs`.
  - Stacks layers by `orderIndex`.
  - Real-time `KeyframeInterpolator`: interpolates animated properties (Position X, Y, Scale X, Y, Rotation Z, Opacity) across keyframes using custom easing curves.
  - Interactive Layer Manipulation: tap to select layer, bounding box with corner anchor points, and drag gestures to move layers on canvas, automatically recording Position keyframes.
- **Multi-Track Timeline Controls**:
  - Track color indicator bars for each layer type.
  - Per-track Visibility toggle (`Visibility` / `VisibilityOff`).
  - Per-track Lock toggle (`Lock` / `LockOpen`).
  - Track duration labels and keyframe count badges.
  - Delete selected layer button.

