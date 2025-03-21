<h1 align="center">CMP-Markdown</h1>
<h2>a markdown renderer include(Android,Ios,Desktop,Web)</h2>
<h3>the lib supports part html_block</h3>

## Download

### Gradle

Add the dependency below to your **module**'s `build.gradle.kts` file:

```gradle
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.kmpstudy:cmp-markdown:0.5.0")
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