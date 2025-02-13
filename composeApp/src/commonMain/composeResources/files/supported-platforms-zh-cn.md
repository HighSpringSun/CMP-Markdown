[//]: # (标题: 受支持平台的稳定性)

Kotlin 多平台技术允许你为各种平台创建应用程序，并在这些平台之间共享代码，这样你就可以覆盖使用他们喜爱设备的用户。不同平台的稳定性可能会有所不同，这取决于核心 Kotlin 多平台技术对代码共享的支持程度以及 Compose 多平台 UI 框架的支持程度。

本页面包含的信息可以帮助你确定哪些平台符合你的项目需求，以及它们的稳定性级别详情。

## 核心 Kotlin 多平台技术的稳定性级别

以下是核心 Kotlin 多平台技术的平台稳定性级别及其含义的快速指南：

**实验性** 意味着 “仅供试用”：

* 我们只是在尝试一个想法，希望一些用户能够使用它并提供反馈。如果效果不理想，我们可能会随时放弃它。

**尽力维护** 意味着 “在大多数场景下可以安全使用”：

* 可能会有意外的重大变更。
* 你在迁移过程中可能会遇到问题。

**稳定** 意味着 “即使在最保守的场景下你也可以使用它”：

* 它已经完成了。我们将根据我们严格的[向后兼容性规则](https://kotlinfoundation.org/language-committee-guidelines/)来进行开发。

### Kotlin Multiplatform 核心技术当前各平台稳定性级别

| 平台                   | 稳定性级别         |
|----------------------|---------------|
| Android              | 稳定            |
| iOS                  | 稳定            |
| 桌面 (JVM)             | 稳定            |
| 服务器端 (JVM)           | 稳定            |
| 基于 Kotlin/Wasm 的 Web | Alpha(内部测试阶段) |
| 基于 Kotlin/JS 的 Web   | 稳定            |
| watchOS              | 尽力维护          |
| tvOS                 | 尽力维护          |

Kotlin 多平台技术支持的原生平台不止上述列出的这些。要了解每个平台的支持级别，请参阅 [Kotlin/原生目标支持](https://kotlinlang.org/docs/native-target-support.html)。

关于 Kotlin 组件（如 Kotlin 多平台技术）的稳定性级别的更多信息，请参阅 [Kotlin 组件的稳定性级别](https://kotlinlang.org/docs/components-stability.html#current-stability-of-kotlin-components)。

## Compose 多平台 UI 框架的稳定性级别

以下是 Compose 多平台 UI 框架的平台稳定性级别及其含义的快速指南：

**实验性** 意味着 “正在开发中”：

* 某些功能可能还不可用，而现有的功能可能存在性能问题或漏洞。
* 未来可能会有变化，并且可能会频繁出现重大变更。

**Alpha(内部测试阶段)** 意味着 “使用风险自负，可能会有迁移问题”：

* 我们已经决定将平台支持产品化，但它还没有最终定型。

**测试版** 意味着 “你可以使用它，我们会尽力为你减少迁移问题”：

* 它几乎完成了，所以现在用户的反馈尤为重要。
* 它还没有 100% 完成，所以可能会有变化（包括根据你的反馈进行的更改）。

我们将 **实验性**、**Alpha(内部测试阶段)** 和 **测试版** 统称为 **预稳定** 级别。

**稳定** 意味着 “即使在最保守的场景下你也可以使用它”：

* 该框架提供了全面的 API 接口，允许你编写美观、可用于生产环境的应用程序，而不会遇到框架本身的性能或其他问题。
* 只有在官方发布弃用公告后的两个版本后，才会进行破坏 API 的更改。

### Compose 多平台 UI 框架的当前平台稳定性级别

| 平台                    | 稳定性级别         |
|-----------------------|---------------|
| Android               | 稳定            |
| iOS                   | 测试版           |
| 桌面 (JVM)              | 稳定            |
| 基于 Kotlin/Wasm 的 Web  | Alpha(内部测试阶段) |

## 接下来做什么？

请参阅 [推荐的 IDE](recommended-ides.md)，了解在不同平台组合的代码共享场景中，哪个 IDE 更适合你。
