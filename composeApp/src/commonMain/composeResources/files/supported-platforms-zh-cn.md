[//]: # (title: 支撑平台的稳定性)

Kotlin Multiplatform(以下简称为KMP)允许你为各种平台创建应用程序并且在它们之间分享代码，以便能够覆盖用户喜欢的设备。
不同的平台可能具有不同的稳定性等级，这取决于KMP的核心代码对代码共享的支持程度，以及Compose Multiplatform(以下简称CMP) UI框架的支持程度。

本页包含的信息将帮助您确定哪些平台符合您的项目需求，并提供有关其稳定性等级的详细信息。

## KMP 核心技术稳定性等级

以下是 Kotlin 多平台核心技术平台稳定性级别的简要指南及其含义：

**实验性** 意味着“仅供试用”:

* 我们只是在尝试一个想法，并希望一些用户试用并提供反馈。如果效果不佳，我们可能会随时放弃。

**尽最大努力** 意味着“在大多数情况下使用是安全的”:

* 可能会有意外的破坏性更改。
* 在迁移过程中可能会遇到问题。

**稳定** 意味着“即使在最保守的场景中也可以使用”:

* 它已经完成。我们将根据严格的[向后兼容性规则](https://kotlinfoundation.org/language-committee-guidelines/)进行开发。

### 当前KMP核心技术各个平台稳定性级别

| Platform                 | Stability level |
|--------------------------|-----------------|
| Android                  | Stable          |
| iOS                      | Stable          |
| Desktop (JVM)            | Stable          |
| Server-side (JVM)        | Stable          |
| Web based on Kotlin/Wasm | Alpha           |
| Web based on Kotlin/JS   | Stable          |
| watchOS                  | Best effort     |
| tvOS                     | Best effort     |

Kotlin Multiplatform supports more native platforms than are listed here. To understand the level of support for each of
them, see [Kotlin/Native target support](https://kotlinlang.org/docs/native-target-support.html).

For more information on the stability levels of Kotlin components like Kotlin Multiplatform,
see [Stability levels of Kotlin components](https://kotlinlang.org/docs/components-stability.html#current-stability-of-kotlin-components).

## Compose Multiplatform UI framework stability levels

Here's a quick guide to platform stability levels for the Compose Multiplatform UI framework and their meaning:

**Experimental** means "it's under development":

* Some features might not be available yet and those features that are present might have performance issues or bugs.
* There might be changes in the future, and breaking changes may occur frequently.

**Alpha** means "use at your own risk, expect migration issues":

* We have decided to productize platform support but it hasn't taken its final shape yet.

**Beta** means "you can use it, and we'll do our best to minimize migration issues for you":

* It's almost done, so user feedback is especially important now.
* It's not 100% finished yet, so changes are possible (including ones based on your own feedback).

We refer to **Experimental**, **Alpha**, and **Beta** collectively as **pre-stable** levels.

**Stable** means "you can use it even in the most conservative of scenarios":

* The framework provides a comprehensive API surface that allows you to write beautiful, production-ready applications,
  without encountering performance or other issues in the framework itself.
* API-breaking changes can only be made 2 versions after an official deprecation announcement.

### Current platform stability levels for Compose Multiplatform UI framework

| Platform                 | Stability level |
|--------------------------|-----------------|
| Android                  | Stable          |
| iOS                      | Beta            |
| Desktop (JVM)            | Stable          |
| Web based on Kotlin/Wasm | Alpha           |

## What's next?

See [Recommended IDEs](recommended-ides.md) to learn which IDE is better for your code-sharing scenario across different
combinations of platforms.
