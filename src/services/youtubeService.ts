import { YouTubeVideoItem } from '../types';

export class YouTubeService {
  public static async searchVideos(query: string, apiKey?: string): Promise<YouTubeVideoItem[]> {
    return [
      {
        id: 'yt_1',
        title: `${query || 'রবীন্দ্রসঙ্গীত'} - সেরা বাংলা গান সংগ্রহ`,
        channelTitle: 'Music Bengal HD',
        duration: '45:20',
        views: '1.2M views',
        thumbnailUrl: 'https://picsum.photos/seed/yt1/320/180',
      },
      {
        id: 'yt_2',
        title: 'MYRA Autonomous AI Agent Demo in Bengali',
        channelTitle: 'BongoLive Tech',
        duration: '12:05',
        views: '250K views',
        thumbnailUrl: 'https://picsum.photos/seed/yt2/320/180',
      },
      {
        id: 'yt_3',
        title: 'Top 10 Tech News in Bangladesh 2026',
        channelTitle: 'Tech Bangla Daily',
        duration: '18:40',
        views: '89K views',
        thumbnailUrl: 'https://picsum.photos/seed/yt3/320/180',
      },
    ];
  }
}
