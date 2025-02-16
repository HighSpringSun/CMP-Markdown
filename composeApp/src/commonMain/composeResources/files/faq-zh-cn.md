[//]: # (title: 常见问题解答)

## Kotlin 多平台

### 什么是 Kotlin 多平台？

[Kotlin 多平台](https://www.jetbrains.com/kotlin-multiplatform/) (KMP) 是 JetBrains 开发的一项开源技术，用于灵活的跨平台开发。它允许您为各种平台创建应用程序，并在这些平台之间高效地重用代码，同时保留原生编程的优势。通过 Kotlin 多平台，您可以开发适用于 Android、iOS、桌面、Web、服务器端和其他平台的应用程序。

### 我可以使用 Kotlin 多平台共享 UI 吗？

是的，您可以使用 [Compose 多平台](https://www.jetbrains.com/lp/compose-multiplatform/) 共享 UI，这是 JetBrains 基于 Kotlin 和 [Jetpack Compose](https://developer.android.com/jetpack/compose) 开发的声明式 UI 框架。该框架允许您为 iOS、Android、桌面和 Web 等平台创建共享的 UI 组件，帮助您在不同设备和平台上保持一致的用户界面。

要了解更多信息，请参阅 [Compose 多平台](#compose-多平台) 部分。

### Kotlin 多平台支持哪些平台？

Kotlin 多平台支持 Android、iOS、桌面、Web、服务器端和其他平台。了解更多关于 [支持的平台](supported-platforms.md)。

### 我应该使用哪个 IDE 来开发跨平台应用程序？

我们建议根据项目需求和期望使用 JetBrains Fleet 代码编辑器或 Android Studio IDE。了解更多关于如何选择的信息，请参阅 [推荐的 IDE 和代码编辑器](recommended-ides.md)。

### 如何创建一个新的 Kotlin 多平台项目？

[创建 Kotlin 多平台应用程序](get-started.topic) 教程提供了创建 Kotlin 多平台项目的逐步说明。您可以决定共享什么——仅逻辑或逻辑和 UI 都共享。

### 我有一个现有的 Android 应用程序。如何将其迁移到 Kotlin 多平台？

[使您的 Android 应用程序在 iOS 上运行](multiplatform-integrate-in-existing-app.md) 逐步教程解释了如何使用原生 UI 使您的 Android 应用程序在 iOS 上运行。如果您还想使用 Compose 多平台共享 UI，请参阅 [相应的答案](#我有一个现有的使用-jetpack-compose-的-android-应用程序-我应该如何将其迁移到其他平台)。

### 我可以在哪里找到完整的示例来尝试？

这里有一个 [真实示例列表](multiplatform-samples.md)。

### 我可以在哪里找到使用 Kotlin 多平台的真实应用程序列表？哪些公司在生产中使用 KMP？

查看我们的 [案例研究列表](case-studies.topic)，了解其他已经在生产中使用 Kotlin 多平台的公司。

### 哪些操作系统可以与 Kotlin 多平台一起使用？

如果您要处理共享代码或平台特定代码（除了 iOS），您可以在 IDE 支持的任何操作系统上工作。

了解更多关于 [推荐的 IDE](recommended-ides.md)。

如果您想编写 iOS 特定代码并在模拟器或真实设备上运行 iOS 应用程序，请使用 macOS 的 Mac。这是因为根据 Apple 的要求，iOS 模拟器只能在 macOS 上运行，而不能在 Microsoft Windows 或 Linux 等其他操作系统上运行。

### 如何在 Kotlin 多平台项目中编写并发代码？

您仍然可以使用协程和流在 Kotlin 多平台项目中编写异步代码。如何调用此代码取决于您从何处调用代码。从 Kotlin 代码调用挂起函数和流已被广泛记录，尤其是对于 Android。[从 Swift 代码调用它们](https://kotlinlang.org/docs/native-ios-integration.html#calling-kotlin-suspending-functions) 需要更多的工作，请参阅 [KT-47610](https://youtrack.jetbrains.com/issue/KT-47610) 了解更多详细信息。

目前从 Swift 调用挂起函数和流的最佳方法是使用插件和库，如 [KMP-NativeCoroutines](https://github.com/rickclephas/KMP-NativeCoroutines) 或 [SKIE](https://skie.touchlab.co/) 以及 Swift 的 `async`/`await` 或库如 Combine 和 RxSwift。目前，KMP-NativeCoroutines 是更经过验证的解决方案，它支持 `async`/`await`、Combine 和 RxSwift 并发方法。SKIE 设置更简单且更简洁。例如，它将 Kotlin `Flow` 直接映射到 Swift `AsyncSequence`。这两个库都支持协程的正确取消。

要了解如何使用它们，请参阅 [在 iOS 和 Android 之间共享更多逻辑](multiplatform-upgrade-app.md)。

### 什么是 Kotlin/Native，它与 Kotlin 多平台有什么关系？

[Kotlin/Native](https://kotlinlang.org/docs/native-overview.html) 是一种将 Kotlin 代码编译为原生二进制文件的技术，这些二进制文件可以在没有虚拟机的情况下运行。它包括 Kotlin 编译器的 [LLVM 后端](https://llvm.org/) 和 Kotlin 标准库的原生实现。

Kotlin/Native 主要用于允许在不希望或不可能使用虚拟机的平台上进行编译，例如嵌入式设备和 iOS。当您需要生成一个不需要额外运行时或虚拟机的自包含程序时，它特别适合。

例如，在移动应用程序中，用 Kotlin 编写的共享代码通过 Kotlin/JVM 编译为 Android 的 JVM 字节码，并通过 Kotlin/Native 编译为 iOS 的原生二进制文件。这使得在 Android 和 iOS 平台上与 Kotlin 多平台的无缝集成成为可能。

![Kotlin/Native 和 Kotlin/JVM 二进制文件](kotlin-native-and-jvm-binaries.png){width=350}

### 如何加快 Kotlin 多平台模块在原生平台（iOS、macOS、Linux）上的编译速度？

请参阅这些 [提高 Kotlin/Native 编译时间的技巧](https://kotlinlang.org/docs/native-improving-compilation-time.html)。

## Compose 多平台

### 什么是 Compose 多平台？

[Compose 多平台](https://www.jetbrains.com/lp/compose-multiplatform/) 是 JetBrains 开发的现代声明式和响应式 UI 框架，它提供了一种简单的方法来用少量的 Kotlin 代码构建用户界面。它还允许您编写一次 UI 并在任何支持的平台上运行——iOS、Android、桌面（Windows、macOS、Linux）和 Web。

### 它与 Android 的 Jetpack Compose 有什么关系？

Compose 多平台与 [Jetpack Compose](https://developer.android.com/jetpack/compose) 共享大部分 API，Jetpack Compose 是 Google 开发的 Android UI 框架。事实上，当您使用 Compose 多平台来针对 Android 时，您的应用程序只需在 Jetpack Compose 上运行。Compose 多平台针对的其他平台可能在底层实现细节上与 Android 上的 Jetpack Compose 不同，但它们仍然为您提供相同的 API。

### 我可以在哪些平台之间共享我的 UI？

我们希望您能够在任何流行的平台组合之间共享您的 UI——Android、iOS、桌面（Linux、macOS、Windows）和 Web（基于 Wasm）。然而，目前 Compose 多平台仅在 Android 和桌面上稳定。有关更多详细信息，请参阅 [支持的平台](supported-platforms.md)。

### 我可以在生产中使用 Compose 多平台吗？

Compose 多平台的 Android 和桌面目标已稳定。您可以在生产中使用它们。

iOS 目标处于 Beta 阶段，这意味着它功能完整。您可以在生产中使用它，并预计迁移问题最小，但请注意更改和弃用警告。

基于 WebAssembly 的 Compose 多平台 Web 版本处于 Alpha 阶段，这意味着它正在积极开发中。您可以谨慎使用它，并预计会有迁移问题。它具有与 Compose 多平台 iOS、Android 和桌面相同的 UI。

### 如何创建一个新的 Compose 多平台项目？

[创建具有共享逻辑和 UI 的 Compose 多平台应用程序](compose-multiplatform-create-first-app.md) 教程提供了为 Android、iOS 和桌面创建 Kotlin 多平台项目的逐步说明。您还可以观看 Kotlin 开发者倡导者 Sebastian Aigner 在 YouTube 上创建的 [视频教程](https://www.youtube.com/watch?v=5_W5YKPShZ4)。

### 我应该使用哪个 IDE 来构建 Compose 多平台应用程序？

我们建议根据项目需求和期望使用 JetBrains Fleet 代码编辑器或 Android Studio IDE。要尝试新的多平台体验，而无需在不同的 IDE 之间切换并切换到 Xcode 编写 Swift 代码，请尝试 [JetBrains Fleet 教程](fleet.md)。

有关如何选择的更多详细信息，请参阅 [推荐的 IDE 和代码编辑器](recommended-ides.md)。

### 我可以尝试演示应用程序吗？在哪里可以找到它？

您可以尝试我们的 [示例](multiplatform-samples.md)。

### Compose 多平台是否带有小部件？

是的，Compose 多平台完全支持 [Material 3](https://m3.material.io/) 小部件。

### 我可以在多大程度上自定义 Material 小部件的外观？

您可以使用 Material 的主题功能来自定义颜色、字体和填充。如果您想创建独特的设计，您可以创建自定义小部件和布局。

### 我可以在现有的 Kotlin 多平台应用程序中共享 UI 吗？

如果您的应用程序使用原生 API 作为其 UI（这是最常见的情况），您可以逐步将某些部分重写为 Compose 多平台，因为它提供了互操作性。您可以使用特殊的互操作视图替换原生 UI，该视图包装了使用 Compose 编写的通用 UI。

### 我有一个现有的使用 Jetpack Compose 的 Android 应用程序。我应该如何将其迁移到其他平台？

应用程序的迁移包括两部分：迁移 UI 和迁移逻辑。迁移的复杂性取决于应用程序的复杂性以及您使用的 Android 特定库的数量。您可以将大部分屏幕迁移到 Compose 多平台而无需更改。所有 Jetpack Compose 小部件都受支持。但是，某些 API 仅在 Android 目标上工作——它们可能是 Android 特定的，或者尚未移植到其他平台。例如，资源处理是 Android 特定的，因此您需要迁移到 [Compose 多平台资源库](compose-multiplatform-resources.md) 或使用社区解决方案。Android [导航库](https://developer.android.com/jetpack/androidx/releases/navigation) 也是 Android 特定的，但有 [社区替代方案](compose-navigation-routing.md) 可用。有关仅适用于 Android 的组件的更多信息，请参阅当前的 [Android 专用 API 列表](compose-android-only-components.md)。

您需要 [将业务逻辑迁移到 Kotlin 多平台](multiplatform-integrate-in-existing-app.md)。当您尝试将代码移动到共享模块时，使用 Android 依赖项的部分将停止编译，您需要重写它们。

* 您可以将使用 Android 专用依赖项的代码重写为使用多平台库。某些库可能已经支持 Kotlin 多平台，因此无需更改。您可以查看 [KMP-awesome](https://github.com/terrakok/kmp-awesome) 库列表。
* 或者，您可以将通用代码与平台特定逻辑分开，并 [提供通用接口](multiplatform-connect-to-apis.md)，这些接口根据平台的不同而实现不同。在 Android 上，实现可以使用您现有的功能，而在其他平台（如 iOS）上，您需要为通用接口提供新的实现。

### 我可以将 Compose 屏幕集成到现有的 iOS 应用程序中吗？

是的。Compose 多平台支持不同的集成场景。有关与 iOS UI 框架集成的更多信息，请参阅 [与 SwiftUI 集成](compose-swiftui-integration.md) 和 [与 UIKit 集成](compose-uikit-integration.md)。

### 我可以将 UIKit 或 SwiftUI 组件集成到 Compose 屏幕中吗？

是的，您可以。请参阅 [与 SwiftUI 集成](compose-swiftui-integration.md) 和 [与 UIKit 集成](compose-uikit-integration