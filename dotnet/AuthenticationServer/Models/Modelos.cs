using System.Text.Json.Serialization;

namespace AuthenticationServer.Models;

public class LoginModel {
    public string Username { get; set; }
    public string Password { get; set; }
}

public class AuthToken {
    public bool Success { get; set; } = false;
    [JsonPropertyName("token_type")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public string TokenType { get; set; }
    [JsonPropertyName("access_token")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public string AccessToken { get; set; }
    [JsonPropertyName("refresh_token")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public string RefreshToken { get; set; }
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public string Name { get; set; }
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public List<string> Roles { get; set; }
    [JsonPropertyName("expires_in")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public int ExpiresIn { get; set; }
}

public class RefreshToken {
    public string Token { get; set; }
}

