# IA-Image-Text Microservice

This microservice provides AI-powered capabilities for text chat and image analysis, specifically tailored for hair style recommendations. It leverages powerful AI providers like **Google Gemini** and **OpenAI**, integrated with **Cloudinary** for image management.

## 🚀 Getting Started

### Prerequisites

*   **Java 21**
*   **Maven**

### Environment Configuration

Before running the application, you need to set up the following environment variables. You can set them in your IDE or as system environment variables.

| Variable | Description | Required |
| :--- | :--- | :--- |
| `CLOUDINARY_CLOUD_NAME` | Your Cloudinary Cloud Name | Yes |
| `CLOUDINARY_API_KEY` | Your Cloudinary API Key | Yes |
| `CLOUDINARY_API_SECRET` | Your Cloudinary API Secret | Yes |
| `IA_PROVIDER` | AI Provider to use (`gemini` or `openai`) | No (Default: `gemini`) |
| `GEMINI_API_KEY` | API Key for Google Gemini (if provider is `gemini`) | Yes (if using Gemini) |
| `OPENAI_API_KEY` | API Key for OpenAI (if provider is `openai`) | Yes (if using OpenAI) |

### Running Locally

To run the application locally, use the following Maven command:

```bash
./mvnw spring-boot:run
```

The server will start on port `8080` (default).

---

## 📚 API Reference

### 1. Chat with AI

Interact with the AI assistant for text-based recommendations or general queries.

*   **Endpoint:** `/api/chat/ask`
*   **Method:** `POST`
*   **Content-Type:** `application/json`

#### Request Body

```json
{
  "userMessage": "I need a haircut for a round face, what do you recommend?",
  "recommendationContext": "The user prefers short hair and low maintenance styles."
}
```

| Field | Type | Description |
| :--- | :--- | :--- |
| `userMessage` | String | The actual message or question from the user. |
| `recommendationContext` | String | Optional context to help the AI give better advice (e.g., face shape, preferences). |

#### Response Body

```json
{
  "reply": "For a round face, I recommend a textured crop or a pompadour with faded sides to add height and elongate the face shape. Avoid buzz cuts as they can accentuate roundness."
}
```

---

### 2. Analyze Haircut from Image

Upload an image to analyze hair capabilities and get style recommendations based on visual data.

*   **Endpoint:** `/api/haircut/analyze`
*   **Method:** `POST`
*   **Content-Type:** `multipart/form-data`

#### Request Parameters (Form Data)

| Key | Type | Description |
| :--- | :--- | :--- |
| `file` | File | The image file to analyze (JPG, PNG, etc.). |
| `userId` | String | (Optional) Unique ID of the user. Defaults to "anonymous". |

#### Response Body

```json
{
  "recommendedStyle": "Modern Quiff",
  "confidenceLevel": "95%",
  "analysisReport": "The user has thick, wavy hair which is perfect for voluminous styles like a Quiff. The facial structure appears oval."
}
```

| Field | Type | Description |
| :--- | :--- | :--- |
| `recommendedStyle` | String | The specific haircut style suggested by the AI. |
| `confidenceLevel` | String | The AI's confidence score for this recommendation. |
| `analysisReport` | String | A detailed explanation of why this style was chosen, based on hair type and face shape analysis. |
