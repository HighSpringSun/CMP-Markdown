<h1 align="center">CMP-Markdown</h1>


## Download

### Gradle

Add the dependency below to your **module**'s `build.gradle.kts` file:

```gradle
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.kmpstudy:cmp-markdown:0.3.0")
        }
    }
}
```

## Usage

```kotlin
   val markdownContent = """
      ## Download
   """.trimIndent()
   MarkdownParser(markdownContent).parse().invoke()
```