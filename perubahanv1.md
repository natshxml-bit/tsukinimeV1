# Perubahan v1 — TsukiNime Android (Kotlin + Jetpack Compose)

> Dokumentasi semua perubahan dari rilis awal `v1.0.0` sampai `v1.1.4`,
> termasuk fix backend streaming. Update terakhir: 12 Agustus 2026.

---

## v1.1.4 — Rewrite UI match Next.js reference

**Commit:** `d16ea9e` · **Tag:** `v1.1.4`

### Home Screen
- **HeroCarousel** — rewrite total gaya UTM (referensi Next.js):
  - Backdrop blur banner aktif di belakang slide (alpha 0.45, scale 1.06)
  - Gradient overlay → Bg di tepi
  - HorizontalPager dengan animasi scale (0.94→1.0) + alpha (0.5→1.0)
  - Glass box `rgba(20,20,20,0.85)` border white 0.1 berisi:
    - Amber rating box `#FFC107` (dark text, star icon)
    - Status pill cyan outline (`EPISODE`/`COMPLETED`)
    - Type pill white 0.2
    - Genre pills (max 2)
    - Synopsis 2 lines
  - Dual CTA: Tonton (1.6f filled accent) + Nobar (1f dark translucent outline)
  - Dot indicator animasi: active 14dp pill accent, inactive 6dp circle
- **GenreChipsRow** — rewrite style chip referensi:
  - Bg `#17171A`, border white 0.12
  - Icon `Sell` accent 0.7 + text abu
  - Rounded pill, padding 15/8

### Detail Screen
- **Full rewrite** mengikuti referensi Next.js detail page:
  - **Hero banner** 330dp + gradient ke Bg, back button 45dp circle blur semi-transparan
  - **Poster overlap** 110dp 2:3, border accent 0.35 2dp, shadow — menjorok keluar banner via `align(BottomStart)` + `offset`
  - **Title** 22sp Black weight, text shadow, 3 lines
  - **Status badge** cyan (ongoing) / gold (completed), type badge accent, rating amber star (skip null/0)
  - **Meta row** kartu 3 kolom (Rilis/Author/Total) dengan Material icons (CalendarMonth, Sell, Bookmark) + divider
  - **Genre chips** outline accent, rounded pill
  - **Schedule badge** `📅 Setiap Rabu` (hanya ongoing)
  - **CTA** Tonton Sekarang (1.8f filled) + Subscribe (1f tonal, bell toggle)
  - **Views/subscriber counter** format `12.3K`/`1.2M`, skip null
  - **Sinopsis** accent bar + judul, 3-line expand/collapse dengan chevron rotate
  - **Episode cards** — ep-num badge 40dp (accent border, watched state solid), title, views·date, Play pill accent
  - Episode search collapsible, pagination 20/load
- Hapus dead code: `MetaChipsRow`, `InfoChip`, `DetailInfoPills`, `addNotNull`
- Fix import: `RectangleShape` dari `androidx.compose.ui.graphics`

---

## v1.1.3 — Badge lebih kecil + rating disamakan

**Commit:** `b2cb887` · **Tag:** `v1.1.3`

### AnimeCard
- Status badge (NEW/ONGOING/PICKS) diperkecil: font 10sp→8sp, padding 10/4→6/2
- Rating badge disamakan style dengan status badge:
  - Shape mirror `RoundedCornerShape(topEnd=8, bottomStart=8)` (lawan dari status di TopStart)
  - Bg solid black 0.75 (bukan translucent 0.5)
  - Star 8dp, font 8sp — konsisten dengan status badge
  - Posisi flush di pojok kanan-atas tanpa margin (sebelumnya padding 6dp)

---

## v1.1.2 — Fix A6.2 + B2 + B3 (Detail v3)

**Commit:** `5b82639` · **Tag:** `v1.1.2`

### AnimeCard (A6.2)
- Rating badge pindah ke `TopEnd` (sebelumnya dempet dengan status badge di TopStart)
- Shape full rounded 8dp, bg black 0.5, padding 6/4, star + white bold 10sp
- Status badge tetap `TopStart` — tidak lagi nabrak

### DetailScreen (B2)
- Poster: hapus `.background(SurfaceAlt)` + `.border` box persist di belakang gambar
- Sekarang satu `AsyncImage` langsung dengan `aspectRatio(2f/3f).clip(RoundedCornerShape(8.dp))`
- Placeholder hitam yang ga hilang setelah load → teratasi

### DetailScreen (B3)
- Metadata chip dengan icon + FlowRow wrap (📅🎬🏢📀)
- `@OptIn(ExperimentalLayoutApi)` untuk FlowRow
- Genre tags tetap outline accent tanpa icon

---

## v1.1.1 — Fix A0 + A6.1 + bg premium

**Commit:** `615ec25` · **Tag:** `v1.1.1`

### HomeSections (A0 — grid orphan row)
- Grid preview wajib kelipatan kolom (3, 6, 9...)
- `displayItems = items.take((items.size / columns) * columns)`
- Skip section total kalau hasil potong < 1 baris penuh
- Berlaku semua section: Episode Terbaru, Sedang Tayang, Rekomendasi
- Item ter-trim tetap ada di halaman "Semua" (full catalog boleh nyisa)

### AnimeCard (A6.1 — badge flat solid)
- Badge status ganti dari ribbon/rounded ke flat solid rectangle
- `RoundedCornerShape(topStart=8.dp, bottomEnd=8.dp)` — 2 sudut rounded
- Nempel pojok kiri-atas tanpa margin, no shadow/border
- Text putih bold 10sp, padding 10/4
- `CardBadge.foreground` param dihapus (text hardcoded white)

### Theme (bg premium)
- `Bg = 0xFF000000` (pitch black, sebelumnya `0xFF0A0A0A`)
- `Surface = 0xFF0C0C0C`, `SurfaceAlt = 0xFF171717`

---

## v1.1.0 — Fix onTextLayout compile

**Commit:** `5c41057` · **Tag:** `v1.1.0`

### DetailScreen
- Hapus import `androidx.compose.ui.text.onTextLayout` (Unresolved reference)
- `onTextLayout` adalah parameter `Text` composable, bukan Modifier extension
- Build CI jadi hijau setelah fix ini

---

## v1.0.0 — Rilis awal Home + Detail v2

**Commit:** `7e8a558` · **Tag:** `v1.0.0`

### Home Screen v2
- **HomeHeader**: avatar 48dp (guest solid tanpa border / login border accent), greeting dinamis by jam, badge Lv.X + tier gap 8dp, search bar pill 48dp
- **HeroCarousel**: 16:9, gradient→Bg, rating skip null, CTA Tonton 1.6f + Nobar 1f, dot animasi
- **CheckInCard**: border accent, bg tint gelap, icon calendar, DONE/CLAIM
- **GenreChipsRow**: LazyRow, chip outline accent
- **ContinueWatchingRow**: 16:9, progress bar 3dp, badge episode
- **HomeSections**: grid via BoxWithConstraints, shimmer 6 kartu

### Data Layer
- `Models.kt`: `AnimeItem.hasRating` guard (rating > 0)
- `LocalStore`: `UserProfile.avatarUrl`, `watchedEpisodes` Set + `markWatched`/`isWatched`
- `PlayerScreen`: markWatched + pushHistory simpan poster

### Detail Screen v2
- `AnimeDetail` model + `Episode` + `toDetail(watchedSet)`
- `DetailViewModel` + `DetailViewModelFactory`
- Collapsing sticky header (200ms animasi)
- Hero 16:9 + poster overlay, metadata chips, genre tags, CTA, sinopsis expand, related, episode list dengan pagination + search

---

## Backend — Fix Streaming "Video Tidak Ditemukan"

**Repo:** `backendnime` · **Commits:** `72823fe`, `6d826c0`, `0aa72d8`, `6e551e8`

### Root cause
`adapter.episode()` memanggil `verifyStreams()` → `headCheck()` melakukan HTTP HEAD ke URL stream **dari server Railway (IP datacenter)**. Upstream menolak IP server → semua mirror `ok=false` → `direct=null` → `streamUrl=null`. Selain itu, field `server` dikirim sebagai object `{qualities}` padahal app expect `String?` → decode gagal → `EpisodeDetail=null` → "Video tidak ditemukan".

### Fix
- **Skip verifikasi server** — hapus `verifyStreams()`, pakai `qualities` mentah dari `qualityFromStreams`
- **Fix field type** — `server: null` (bukan object), `servers` diisi list `[{server, qualities:[{quality,url}]}]` sesuai kontrak app
- URL stream langsung diputar oleh ExoPlayer di device user → request keluar dari **IP user**, upstream menerima
- **Cache-bust** endpoint `/admin/cache-bust-ep` — hapus 12,532 key `ep:*` yang masih simpan response lama
- Dokumentasi: `BACKEND_STREAMING_FIX.md` (commit `6e6a3cb`) — sudah disetujui tim agent

### Hasil
- `streamUrl` terisi URL valid (storage animekita / pixeldrain)
- `server: null` → app bisa decode `EpisodeDetail`
- `servers: 3` (720p/480p/360p dengan mirror)
- Video jalan langsung dari device user (IP user)

---

## File yang berubah (ringkasan)

| File | Perubahan |
|------|-----------|
| `theme/Theme.kt` | Bg pitch black `0xFF000000` |
| `ui/components/AnimeCard.kt` | Badge A6.1 flat solid + A6.2 rating TopEnd + ukuran kecil |
| `ui/home/components/HeroCarousel.kt` | Rewrite total UTM style |
| `ui/home/components/HomeExtras.kt` | GenreChipsRow rewrite chip style |
| `ui/home/components/HomeSections.kt` | A0 grid trim kelipatan 3 |
| `ui/detail/DetailScreen.kt` | Full rewrite match Next.js reference |
| `ui/player/PlayerScreen.kt` | (tidak berubah — streaming fix di backend) |
| `backendnime/adapter.js` | `episode()` skip verify + fix server field type |
| `backendnime/app.js` | Endpoint cache-bust `/admin/cache-bust-ep` |
| `backendnime/BACKEND_STREAMING_FIX.md` | Dokumentasi fix streaming |
