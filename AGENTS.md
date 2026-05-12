# AGENTS

<skills_system priority="1">

## Available Skills

<!-- SKILLS_TABLE_START -->
<usage>
When users ask you to perform tasks, check if any of the available skills below can help complete the task more effectively. Skills provide specialized capabilities and domain knowledge.

How to use skills:
- Invoke: `npx openskills read <skill-name>` (run in your shell)
  - For multiple: `npx openskills read skill-one,skill-two`
- The skill content will load with detailed instructions on how to complete the task
- Base directory provided in output for resolving bundled resources (references/, scripts/, assets/)

Usage notes:
- Only use skills listed in <available_skills> below
- Do not invoke a skill that is already loaded in your context
- Each skill invocation is stateless
</usage>

<available_skills>

<skill>
<name>baoyu-article-illustrator</name>
<description>Smart article illustration skill. Analyzes article content and generates illustrations at positions requiring visual aids with multiple style options. Use when user asks to "add illustrations to article", "generate images for article", or "illustrate article".</description>
<location>global</location>
</skill>

<skill>
<name>baoyu-comic</name>
<description>Knowledge comic creator supporting multiple styles (Logicomix/Ligne Claire, Ohmsha manga guide). Creates original educational comics with detailed panel layouts and sequential image generation. Use when user asks to create "知识漫画", "教育漫画", "biography comic", "tutorial comic", or "Logicomix-style comic".</description>
<location>global</location>
</skill>

<skill>
<name>baoyu-compress-image</name>
<description>Cross-platform image compression skill. Converts images to WebP by default with PNG-to-PNG support. Uses system tools (sips, cwebp, ImageMagick) with Sharp fallback.</description>
<location>global</location>
</skill>

<skill>
<name>baoyu-cover-image</name>
<description>Generate elegant cover images for articles. Analyzes content and creates eye-catching hand-drawn style cover images with multiple style options. Use when user asks to "generate cover image", "create article cover", or "make a cover for article".</description>
<location>global</location>
</skill>

<skill>
<name>baoyu-danger-gemini-web</name>
<description>Image generation skill using Gemini Web. Generates images from text prompts via Google Gemini. Also supports text generation. Use as the image generation backend for other skills like cover-image, xhs-images, article-illustrator.</description>
<location>global</location>
</skill>

<skill>
<name>baoyu-danger-x-to-markdown</name>
<description>Convert X (Twitter) tweet or article URL to markdown. Uses reverse-engineered X API (private). Requires user consent before use.</description>
<location>global</location>
</skill>

<skill>
<name>baoyu-post-to-wechat</name>
<description>Post content to WeChat Official Account (微信公众号). Supports both article posting (文章) and image-text posting (图文).</description>
<location>global</location>
</skill>

<skill>
<name>baoyu-post-to-x</name>
<description>Post content and articles to X (Twitter). Supports regular posts with images and X Articles (long-form Markdown). Uses real Chrome with CDP to bypass anti-automation.</description>
<location>global</location>
</skill>

<skill>
<name>baoyu-slide-deck</name>
<description>Generate professional slide deck images from content. Creates comprehensive outlines with style instructions, then generates individual slide images. Use when user asks to "create slides", "make a presentation", "generate deck", or "slide deck".</description>
<location>global</location>
</skill>

<skill>
<name>baoyu-xhs-images</name>
<description>Xiaohongshu (Little Red Book) infographic series generator with multiple style options. Breaks down content into 1-10 cartoon-style infographics. Use when user asks to create "小红书图片", "XHS images", or "RedNote infographics".</description>
<location>global</location>
</skill>

<skill>
<name>docx</name>
<description>"Comprehensive document creation, editing, and analysis with support for tracked changes, comments, formatting preservation, and text extraction. When Claude needs to work with professional documents (.docx files) for: (1) Creating new documents, (2) Modifying or editing content, (3) Working with tracked changes, (4) Adding comments, or any other document tasks"</description>
<location>global</location>
</skill>

<skill>
<name>excalidraw-diagram</name>
<description>Generate Excalidraw diagrams from text content for Obsidian. Use when user asks to create diagrams, flowcharts, mind maps, or visual representations in Excalidraw format. Triggers on "Excalidraw", "画图", "流程图", "思维导图", "可视化", "diagram".</description>
<location>global</location>
</skill>

<skill>
<name>json-canvas</name>
<description>Create and edit JSON Canvas files (.canvas) with nodes, edges, groups, and connections. Use when working with .canvas files, creating visual canvases, mind maps, flowcharts, or when the user mentions Canvas files in Obsidian.</description>
<location>global</location>
</skill>

<skill>
<name>mermaid-visualizer</name>
<description>Transform text content into professional Mermaid diagrams for presentations and documentation. Use when users ask to visualize concepts, create flowcharts, or make diagrams from text. Supports process flows, system architectures, comparisons, mindmaps, and more with built-in syntax error prevention.</description>
<location>global</location>
</skill>

<skill>
<name>obsidian-bases</name>
<description>Create and edit Obsidian Bases (.base files) with views, filters, formulas, and summaries. Use when working with .base files, creating database-like views of notes, or when the user mentions Bases, table views, card views, filters, or formulas in Obsidian.</description>
<location>global</location>
</skill>

<skill>
<name>obsidian-canvas-creator</name>
<description>Create Obsidian Canvas files from text content, supporting both MindMap and freeform layouts. Use this skill when users want to visualize content as an interactive canvas, create mind maps, or organize information spatially in Obsidian format.</description>
<location>global</location>
</skill>

<skill>
<name>obsidian-markdown</name>
<description>Create and edit Obsidian Flavored Markdown with wikilinks, embeds, callouts, properties, and other Obsidian-specific syntax. Use when working with .md files in Obsidian, or when the user mentions wikilinks, callouts, frontmatter, tags, embeds, or Obsidian notes.</description>
<location>global</location>
</skill>

<skill>
<name>pdf</name>
<description>Comprehensive PDF manipulation toolkit for extracting text and tables, creating new PDFs, merging/splitting documents, and handling forms. When Claude needs to fill in a PDF form or programmatically process, generate, or analyze PDF documents at scale.</description>
<location>global</location>
</skill>

<skill>
<name>pptx</name>
<description>"Presentation creation, editing, and analysis. When Claude needs to work with presentations (.pptx files) for: (1) Creating new presentations, (2) Modifying or editing content, (3) Working with layouts, (4) Adding comments or speaker notes, or any other presentation tasks"</description>
<location>global</location>
</skill>

<skill>
<name>release-skills</name>
<description>Release workflow for baoyu-skills plugin. This skill should be used when the user wants to create a new release version. It analyzes changes since the last version tag, updates changelogs (EN/CN), bumps the version in marketplace.json, commits changes, and creates a version tag. Supports dry-run mode and breaking change detection.</description>
<location>global</location>
</skill>

<skill>
<name>video-2-ppt</name>
<description>Download video from URL, transcribe audio to text using Whisper, translate to Chinese if needed, generate hierarchical summary, and create rich PPT presentation. Use when user asks to download and process video content, extract speech/subtitles from video and create presentation, convert video URL to PPT, or summarize video content into slides with visual elements.</description>
<location>global</location>
</skill>

<skill>
<name>xlsx</name>
<description>"Comprehensive spreadsheet creation, editing, and analysis with support for formulas, formatting, data analysis, and visualization. When Claude needs to work with spreadsheets (.xlsx, .xlsm, .csv, .tsv, etc) for: (1) Creating new spreadsheets with formulas and formatting, (2) Reading or analyzing data, (3) Modify existing spreadsheets while preserving formulas, (4) Data analysis and visualization in spreadsheets, or (5) Recalculating formulas"</description>
<location>global</location>
</skill>

<skill>
<name>yt-dlp-downloader</name>
<description>Download videos from YouTube, Bilibili, Twitter, and thousands of other sites using yt-dlp. Use when the user provides a video URL and wants to download it, extract audio (MP3), download subtitles, or select video quality. Triggers on phrases like "下载视频", "download video", "yt-dlp", "YouTube", "B站", "抖音", "提取音频", "extract audio".</description>
<location>global</location>
</skill>

</available_skills>
<!-- SKILLS_TABLE_END -->

</skills_system>
